package com.qiaofang.jiagou.crawler.against.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.common.util.DateUtil;
import com.qiaofang.jiagou.crawler.against.config.CrawlerAgainstProperties;
import com.qiaofang.jiagou.crawler.against.constant.RedisKeyConstant;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.entity.MatchRecord;
import com.qiaofang.jiagou.crawler.against.mapper.MatchRecordMapper;
import com.qiaofang.jiagou.crawler.against.param.MatchRecordListParam;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.stub.dto.CrawlerAgainstMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.HttpRequestLogMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.MatchRecordDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchRecordStatusEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import com.qiaofang.jiagou.crawler.against.stub.util.RequestMatchUtil;
import com.qiaofang.jiagou.crawler.against.util.AlertUtil;
import com.qiaofang.jiagou.crawler.against.util.RedisUtil;
import com.qiaofang.jiagou.crawler.against.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 匹配规则处理记录表 服务实现类
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Service
@Slf4j
public class MatchRecordServiceImpl extends ServiceImpl<MatchRecordMapper, MatchRecord> implements IMatchRecordService {


    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Resource
    private CrawlerAgainstProperties crawlerAgainstProperties;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private AlertUtil alertUtil;
    @Resource
    private UserUtil userUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void createRecord(RuleInfoDTO ruleInfoDTO, HttpRequestLogMessageDTO messageDTO, String tallyDimensionMark) {
        if (MatchActionEnum.PASS.equals(ruleInfoDTO.getMatchAction())) {
            return;
        }
        List<RuleMatchConfigDTO> forbiddenDimensionConfigDTOList = ruleInfoDTO.getForbiddenDimensionConfigDTOList();
        if (CollectionUtils.isEmpty(forbiddenDimensionConfigDTOList)) {
            log.error("规则:{}未配置封禁维度", ruleInfoDTO.getId());
            return;
        }
        //组装封禁的维度
        String matchDimensionMark = RequestMatchUtil.assembleMatchDimensionMark(ruleInfoDTO.getId(), forbiddenDimensionConfigDTOList, messageDTO);
        String matchDimensionKey = DigestUtils.md5Hex(matchDimensionMark);
        String lockKey = RedisKeyConstant.LOCK + matchDimensionKey;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            QueryWrapper<MatchRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("match_dimension_key", matchDimensionKey);
            wrapper.eq("status", MatchRecordStatusEnum.PROCESS.getValue());
            List<MatchRecord> matchRecordList = list(wrapper);
            Date now = new Date();
            MatchRecord record;
            //当前匹配计数维度对应的动作还没完成（比如正在封禁中）可能是之前封禁的消息发送失败，或者网关接受失败，网关没有做出对应的封禁或者验证码动作，只需要重新发送动作处理通知即可，且要注意endTime一定不能重新生成
            if (!CollectionUtils.isEmpty(matchRecordList)) {
                record = matchRecordList.get(0);
                log.info("当前匹配计数维度，对应处理正在进行中,matchDimensionMark:{}", matchDimensionMark);
            } else {
                record = assembleMatchRecordEntity(ruleInfoDTO, matchDimensionMark, matchDimensionKey, tallyDimensionMark);
                String alertTitle = "反爬规则被触发";
                String alertText = String.format("反爬规则[%s]被触发,%s分钟请求超过%s次,执行动作:[%s],计数维度:[%s],匹配维度:[%s]",
                        ruleInfoDTO.getRuleName(), ruleInfoDTO.getDetectionDuration(), ruleInfoDTO.getLimitRequestTimes(), ruleInfoDTO.getMatchAction().getDesc(), tallyDimensionMark, matchDimensionMark);
                List<String> warningUserMobileList = getWarningUserMobile(ruleInfoDTO.getWarningUserIdList());
                switch (ruleInfoDTO.getMatchAction()) {
                    case FORBIDDEN:
                        alertUtil.sendRobotMessage(alertTitle, alertText, warningUserMobileList);
                        record.setEndTime(DateUtil.addDate(now, Calendar.MINUTE, ruleInfoDTO.getForbiddenDuration()));
                        save(record);
                        break;
                    case VERIFICATION:
                        redisUtil.set(RedisKeyConstant.VERIFICATION_RECORD + matchDimensionKey, System.currentTimeMillis(), 1, TimeUnit.DAYS);
                        alertUtil.sendRobotMessage(alertTitle, alertText, warningUserMobileList);
                        record.setEndTime(DateUtil.addDate(now, Calendar.HOUR, 12));
                        save(record);
                        break;
                    case WARNING:
                        alertUtil.sendRobotMessage(alertTitle, alertText, warningUserMobileList);
                        record.setStatus(MatchRecordStatusEnum.DONE.getValue());
                        record.setEndTime(new Date());
                        //报警 生成记录后，直接return，不需要后续发送kafka操作
                        save(record);
                        return;
                    default:
                        //其他动作不做处理，直接return
                        return;
                }
            }
            //发送kafka消息通知网关进行对应动作处理
            sendActionNotifyMessage(ruleInfoDTO, matchDimensionMark, matchDimensionKey, record);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送执行对应动作通知kafka消息
     *
     * @param ruleInfoDTO
     * @param matchDimensionMark
     * @param matchDimensionKey
     * @param record
     */
    private void sendActionNotifyMessage(RuleInfoDTO ruleInfoDTO, String matchDimensionMark, String matchDimensionKey, MatchRecord record) {
        CrawlerAgainstMessageDTO messageDTO = new CrawlerAgainstMessageDTO();
        BeanUtils.copyProperties(ruleInfoDTO, messageDTO);
        messageDTO.setRuleType(RuleTypeEnum.DYNAMIC);
        messageDTO.setMatchRecordId(record.getId());
        messageDTO.setMatchDimensionMark(matchDimensionMark);
        messageDTO.setRuleId(ruleInfoDTO.getId());
        messageDTO.setMatchUrlList(StringUtils.isBlank(ruleInfoDTO.getMatchUrlList()) ? Lists.newArrayList() : Arrays.asList(ruleInfoDTO.getMatchUrlList().split(",")));
        messageDTO.setExcludeUrlList(StringUtils.isBlank(ruleInfoDTO.getExcludeUrlList()) ? Lists.newArrayList() : Arrays.asList(ruleInfoDTO.getExcludeUrlList().split(",")));
        messageDTO.setEndTime(record.getEndTime());
        String msg = JSON.toJSONString(messageDTO);
        if (redisUtil.frequencyControl(RedisKeyConstant.ACTION_NOTIFY_LIMIT + matchDimensionKey, 3, TimeUnit.SECONDS)) {
            log.info("发送{}动作消息,msg:{}", ruleInfoDTO.getMatchAction().getDesc(), msg);
            kafkaTemplate.send(crawlerAgainstProperties.getActionNotifyKafkaTopic(), msg);
        }
    }

    /**
     * 组装MatchRecord
     *
     * @param ruleInfoDTO
     * @param matchDimensionMark
     * @param matchDimensionKey
     * @return
     */
    private MatchRecord assembleMatchRecordEntity(RuleInfoDTO ruleInfoDTO, String matchDimensionMark, String matchDimensionKey, String tallyDimensionMark) {
        MatchRecord record = new MatchRecord();
        BeanUtils.copyProperties(ruleInfoDTO, record);
        record.setRuleId(ruleInfoDTO.getId());
        record.setMatchAction(ruleInfoDTO.getMatchAction().getValue());
        record.setMatchDimensionMark(matchDimensionMark);
        record.setMatchDimensionKey(matchDimensionKey);
        record.setTallyDimensionMark(tallyDimensionMark);
        List<RuleMatchConfigDTO> validConfigDTOList = new ArrayList<>(ruleInfoDTO.getAccurateRuleMatchConfigDTOList());
        validConfigDTOList.addAll(ruleInfoDTO.getTallDimensionConfigDTOList());
        record.setMatchConfigSnapshot(JSON.toJSONString(validConfigDTOList));
        record.setStartTime(new Date());
        record.setStatus(MatchRecordStatusEnum.PROCESS.getValue());
        record.setCreateUser("system");
        record.setUpdateUser("system");
        return record;
    }

    /**
     * 获取报警人手机号
     * @param userIdList
     * @return
     */
    private List<String> getWarningUserMobile(List<String> userIdList) {
        try {
            return new ArrayList<>(userUtil.batchGetMobile(userIdList).values());
        } catch (Exception e) {
            log.error("userUtil.batchGetMobile error, userIdList:{}", userIdList, e);
        }
        return Lists.newArrayList();
    }

    @Override
    public void relieveForbidden(Long id, String remark) {
        MatchRecord matchRecord = this.getById(id);
        if (matchRecord == null) {
            throw new IllegalArgumentException("ID为空");
        }
        //如果是需要验证码的记录，需要删除redis里面的标记，保证网关实时解封
        if (MatchActionEnum.VERIFICATION.getValue().equals(matchRecord.getMatchAction())) {
            redisUtil.delete(RedisKeyConstant.VERIFICATION_RECORD + matchRecord.getMatchDimensionKey());
        }
        //先发送kafka消息通知网关解封
        CrawlerAgainstMessageDTO crawlerAgainstMessageDTO = new CrawlerAgainstMessageDTO();
        crawlerAgainstMessageDTO.setRuleType(RuleTypeEnum.DYNAMIC);
        crawlerAgainstMessageDTO.setMatchRecordId(id);
        crawlerAgainstMessageDTO.setMatchDimensionMark(matchRecord.getMatchDimensionMark());
        crawlerAgainstMessageDTO.setRuleId(matchRecord.getRuleId());
        crawlerAgainstMessageDTO.setMatchAction(MatchActionEnum.PASS);
        String msg = JSON.toJSONString(crawlerAgainstMessageDTO);
        log.info("发送解封消息,msg:{}", msg);
        kafkaTemplate.send(crawlerAgainstProperties.getActionNotifyKafkaTopic(), msg);

        MatchRecord updateEntity = new MatchRecord();
        updateEntity.setId(id);
        // 再改数据库状态
        if (MatchRecordStatusEnum.DONE.getValue() == matchRecord.getStatus()) {
            log.warn("当前记录已经处理完成,id:{}", id);
        } else {
            updateEntity.setStatus(MatchRecordStatusEnum.DONE.getValue());
            Date now = new Date();
            if (matchRecord.getEndTime() == null || matchRecord.getEndTime().after(now)) {
                updateEntity.setEndTime(now);
            }
        }
        if (StringUtils.isNotBlank(matchRecord.getRemark())) {
            remark = matchRecord.getRemark().concat(",").concat(remark);
        }
        updateEntity.setRemark(remark);
        this.updateById(updateEntity);
    }

    @Override
    public IPage<MatchRecordDTO> pageRecord(PageDTO page, MatchRecordListParam param) {
        QueryWrapper<MatchRecord> wrapper = new QueryWrapper<>();
        wrapper.eq(param.getStatus() != null, "status", param.getStatus());
        if (param.getMatchAction() != null) {
            wrapper.eq("match_action", param.getMatchAction().getValue());
        }
        if (StringUtils.isNotBlank(param.getMatchDimensionKey())) {
            wrapper.eq("match_dimension_key", param.getMatchDimensionKey().trim());
        }
        if (StringUtils.isNotBlank(param.getMatchDimensionMark())) {
            wrapper.like("match_dimension_mark", param.getMatchDimensionMark().trim());
        }
        wrapper.ge(StringUtils.isNotBlank(param.getStartTimeStart()), "start_time", param.getStartTimeStart());
        wrapper.le(StringUtils.isNotBlank(param.getStartTimeEnd()), "start_time", param.getStartTimeEnd());
        wrapper.orderByDesc("update_time");
        IPage<MatchRecord> pageData = page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        IPage<MatchRecordDTO> result = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        List<MatchRecord> records = pageData.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return result;
        }
        List<MatchRecordDTO> dtoList = records.stream().map(record -> {
            MatchRecordDTO dto = new MatchRecordDTO();
            BeanUtils.copyProperties(record, dto);
            dto.setMatchAction(MatchActionEnum.getEnumByValue(record.getMatchAction()));
            dto.setMatchActionDesc(dto.getMatchAction().getDesc());
            dto.setMatchUrlList(StringUtils.isBlank(record.getMatchUrlList()) ? Lists.newArrayList() : Arrays.asList(record.getMatchUrlList().split(",")));
            dto.setExcludeUrlList(StringUtils.isBlank(record.getExcludeUrlList()) ? Lists.newArrayList() : Arrays.asList(record.getExcludeUrlList().split(",")));
            dto.setRuleMatchConfigDTOList(JSON.parseArray(record.getMatchConfigSnapshot(), RuleMatchConfigDTO.class));
            return dto;
        }).collect(Collectors.toList());
        result.setRecords(dtoList);
        return result;
    }

}
