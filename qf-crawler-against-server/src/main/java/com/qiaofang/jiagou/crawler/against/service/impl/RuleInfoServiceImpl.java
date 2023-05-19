package com.qiaofang.jiagou.crawler.against.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.qiaofang.common.exception.BusinessException;
import com.qiaofang.common.model.page.PageDTO;
import com.qiaofang.core.cacheclient.cacheanno.CoreCache;
import com.qiaofang.core.cacheclient.cacheanno.CoreCacheRemove;
import com.qiaofang.jiagou.crawler.against.config.CrawlerAgainstProperties;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.entity.BaseEntity;
import com.qiaofang.jiagou.crawler.against.entity.RuleInfo;
import com.qiaofang.jiagou.crawler.against.entity.RuleMatchConfig;
import com.qiaofang.jiagou.crawler.against.mapper.RuleInfoMapper;
import com.qiaofang.jiagou.crawler.against.param.RuleListParam;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import com.qiaofang.jiagou.crawler.against.service.IRuleMatchConfigService;
import com.qiaofang.jiagou.crawler.against.stub.dto.CrawlerAgainstMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.*;
import com.qiaofang.jiagou.crawler.against.util.UserUtil;
import com.qiaofang.jiagou.innersso.principal.UserInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.qiaofang.jiagou.crawler.against.stub.enums.RuleMatchConfigTypeEnum.MATCH_DIMENSION;
import static com.qiaofang.jiagou.crawler.against.stub.enums.RuleMatchConfigTypeEnum.MATCH_RULE;

/**
 * <p>
 * 反爬规则表 服务实现类
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Service
@Slf4j
public class RuleInfoServiceImpl extends ServiceImpl<RuleInfoMapper, RuleInfo> implements IRuleInfoService {

    @Autowired
    private IRuleMatchConfigService ruleMatchConfigService;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Resource
    private CrawlerAgainstProperties crawlerAgainstProperties;
    @Autowired
    private UserUtil userUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CoreCacheRemove(key = "'RuleInfoServiceImpl.getAllEnableRule'")
    public void createOrUpdate(RuleInfoDTO ruleInfoDTO) {
        RuleInfo entity = new RuleInfo();
        BeanUtils.copyProperties(ruleInfoDTO, entity);
        entity.setRuleType(ruleInfoDTO.getRuleType().getValue());
        entity.setMatchAction(ruleInfoDTO.getMatchAction().getValue());
        entity.setUpdateUserName(UserInfoHolder.getUserInfo() != null ? UserInfoHolder.getUserInfo().getName() : "system");
        if (CollectionUtils.isNotEmpty(ruleInfoDTO.getWarningUserIdList())) {
            entity.setWarningUserIdList(String.join(",", ruleInfoDTO.getWarningUserIdList()));
        }
        List<RuleMatchConfigDTO> ruleMatchConfigDTOList = ruleInfoDTO.getRuleMatchConfigDTOList();
        List<RuleMatchConfig> ruleMatchConfigList = ruleMatchConfigDTOList.stream().map(dto -> {
            RuleMatchConfig ruleMatchConfig = new RuleMatchConfig();
            BeanUtils.copyProperties(dto, ruleMatchConfig);
            ruleMatchConfig.setMatchCondition(dto.getMatchCondition().getValue());
            if (dto.getLogicalSymbol() != null) {
                ruleMatchConfig.setLogicalSymbol(dto.getLogicalSymbol().getValue());
            }
            return ruleMatchConfig;
        }).collect(Collectors.toList());

        //保存
        this.save(entity);
        ruleMatchConfigList.forEach(e -> e.setRuleId(entity.getId()));
        ruleMatchConfigService.saveBatch(ruleMatchConfigList);
        //如果是编辑，则删除原来的记录即可
        if (ruleInfoDTO.getId() != null) {
            this.removeById(ruleInfoDTO.getId());
            ruleMatchConfigService.remove(new QueryWrapper<RuleMatchConfig>().eq("rule_id", ruleInfoDTO.getId()));
        }
    }

    @Override
    @CoreCacheRemove(key = "'RuleInfoServiceImpl.getAllEnableRule'")
    public void enableOrDisable(Long id, Boolean enable) {
        RuleInfo ruleInfo = this.getById(id);
        if (ruleInfo == null) {
            throw new IllegalArgumentException("规则不存在");
        }
        RuleInfo updateEntity = new RuleInfo();
        updateEntity.setId(id);
        updateEntity.setEnable(BooleanUtils.toBoolean(enable));
        this.updateById(updateEntity);
    }


    @Override
    @CoreCache(timeToLive = 60 * 60 * 4)
    public List<RuleInfoDTO> getAllEnableRule() {
        QueryWrapper<RuleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("enable", 1);
        wrapper.orderByDesc("sort_no");
        return getRuleInfoDTO(list(wrapper));
    }

    /**
     * 获取RuleInfoDTO
     *
     * @param ruleInfoList
     * @return
     */
    private List<RuleInfoDTO> getRuleInfoDTO(List<RuleInfo> ruleInfoList) {
        if (CollectionUtils.isEmpty(ruleInfoList)) {
            return Lists.newArrayList();
        }
        QueryWrapper<RuleMatchConfig> wrapper = new QueryWrapper<>();
        wrapper.in("rule_id", ruleInfoList.stream().map(BaseEntity::getId).collect(Collectors.toList()));
        List<RuleMatchConfig> allRuleMatchConfigList = ruleMatchConfigService.list();
        if (CollectionUtils.isEmpty(allRuleMatchConfigList)) {
            throw new BusinessException("根据规则id找不到规则匹配配置");
        }
        Map<Long, List<RuleMatchConfig>> ruleMatchConfigMap = allRuleMatchConfigList.stream().collect(Collectors.groupingBy(RuleMatchConfig::getRuleId));

        return ruleInfoList.stream().map(ruleInfo -> {
            RuleInfoDTO dto = new RuleInfoDTO();
            BeanUtils.copyProperties(ruleInfo, dto);
            dto.setRuleType(RuleTypeEnum.getEnumByValue(ruleInfo.getRuleType()));
            dto.setMatchAction(MatchActionEnum.getEnumByValue(ruleInfo.getMatchAction()));
            dto.setRuleTypeDesc(dto.getRuleType().getDesc());
            dto.setMatchActionDesc(dto.getMatchAction().getDesc());
            if (StringUtils.isNotBlank(ruleInfo.getWarningUserIdList())) {
                dto.setWarningUserIdList(Arrays.asList(ruleInfo.getWarningUserIdList().split(",")));
            }
            List<RuleMatchConfig> ruleMatchConfigList = ruleMatchConfigMap.get(ruleInfo.getId());
            if (CollectionUtils.isEmpty(ruleMatchConfigList)) {
                throw new BusinessException(String.format("根据规则id:%s找不到规则匹配配置", ruleInfo.getId()));
            }
            List<RuleMatchConfigDTO> ruleMatchConfigDTOList = ruleMatchConfigList.stream().map(ruleMatchConfig -> {
                RuleMatchConfigDTO ruleMatchConfigDTO = new RuleMatchConfigDTO();
                BeanUtils.copyProperties(ruleMatchConfig, ruleMatchConfigDTO);
                ruleMatchConfigDTO.setMatchCondition(MatchConditionEnum.getEnumByValue(ruleMatchConfig.getMatchCondition()));
                ruleMatchConfigDTO.setLogicalSymbol(LogicalSymbolEnum.getEnumByValue(ruleMatchConfig.getLogicalSymbol()));
                ruleMatchConfigDTO.setRuleMatchConfigTypeEnum(ruleMatchConfig.getLogicalSymbol() == null ? RuleMatchConfigTypeEnum.MATCH_DIMENSION : RuleMatchConfigTypeEnum.MATCH_RULE);
                return ruleMatchConfigDTO;
            }).collect(Collectors.toList());
            //过滤重复的规则
            List<String> markList = Lists.newArrayList();
            ruleMatchConfigDTOList = ruleMatchConfigDTOList.stream().filter(e -> {
                String mark = e.getMatchCondition().getValue().concat(e.getMatchConditionDetail() == null ? "" : e.getMatchConditionDetail());
                if (!markList.contains(mark)) {
                    markList.add(mark);
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            //精准匹配规则
            List<RuleMatchConfigDTO> accurateRuleMatchConfigDTOList = ruleMatchConfigDTOList.stream().filter(e -> MATCH_RULE.equals(e.getRuleMatchConfigTypeEnum())).collect(Collectors.toList());
            //计数维度
            List<RuleMatchConfigDTO> tallDimensionConfigDTOList = ruleMatchConfigDTOList.stream().filter(e -> MATCH_DIMENSION.equals(e.getRuleMatchConfigTypeEnum())).collect(Collectors.toList());
            //封禁维度
            List<RuleMatchConfigDTO> forbiddenDimensionConfigDTOList = tallDimensionConfigDTOList.stream().filter(e -> BooleanUtils.toBoolean(e.getForbiddenFlag())).collect(Collectors.toList());
            dto.setRuleMatchConfigDTOList(ruleMatchConfigDTOList);
            dto.setAccurateRuleMatchConfigDTOList(accurateRuleMatchConfigDTOList);
            dto.setTallDimensionConfigDTOList(tallDimensionConfigDTOList);
            dto.setForbiddenDimensionConfigDTOList(forbiddenDimensionConfigDTOList);
            dto.setRuleMatchConfigDesc(getMatchConfigDesc(ruleMatchConfigDTOList));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 根据配置生成匹配规则可读描述
     *
     * @param ruleMatchConfigDTOList 匹配规则
     * @return
     */
    private String getMatchConfigDesc(List<RuleMatchConfigDTO> ruleMatchConfigDTOList) {
        StringBuilder builder = new StringBuilder();
        for (RuleMatchConfigDTO dto : ruleMatchConfigDTOList) {
            builder.append(dto.getMatchCondition());
            if (MatchConditionEnum.HEADER.equals(dto.getMatchCondition()) && StringUtils.isNotBlank(dto.getMatchConditionDetail())) {
                builder.append(":").append(dto.getMatchConditionDetail());
            }
            if (dto.getLogicalSymbol() != null) {
                builder.append(dto.getLogicalSymbol().getDesc());
                builder.append(dto.getMatchContent());
            }
            builder.append(" & ");
        }
        return builder.substring(0, builder.length() - 3);
    }

    @Override
    public IPage<RuleInfoDTO> page(RuleListParam param, PageDTO pageDTO) {
        QueryWrapper<RuleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(param.getRuleName()), "rule_name", param.getRuleName().trim());
        if (param.getRuleType() != null) {
            queryWrapper.eq("rule_type", param.getRuleType().getValue());
        }
        if (param.getMatchAction() != null) {
            queryWrapper.eq("match_action", param.getMatchAction().getValue());
        }
        if (CollectionUtils.isNotEmpty(param.getOriginList())) {
            queryWrapper.in("origin", param.getOriginList());
        }
        queryWrapper.eq(param.getEnable() != null, "enable", BooleanUtils.toBoolean(param.getEnable()) ? 1 : 0);
        queryWrapper.orderByDesc("enable", "id", "sort_no");
        IPage<RuleInfo> pageData = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
        IPage<RuleInfoDTO> result = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        result.setRecords(getRuleInfoDTO(pageData.getRecords()));
        return result;
    }

    @Override
    public RuleInfoDTO detail(Long id) {
        RuleInfo ruleInfo = this.getById(id);
        if (ruleInfo == null) {
            throw new IllegalArgumentException("规则不存在");
        }
        RuleInfoDTO infoDTO = getRuleInfoDTO(Collections.singletonList(ruleInfo)).get(0);
        if (CollectionUtils.isNotEmpty(infoDTO.getWarningUserIdList())) {
            infoDTO.setUserList(userUtil.searchUser(String.join(",", infoDTO.getWarningUserIdList())));
        }
        return infoDTO;
    }

    @Override
    @CoreCacheRemove(key = "'RuleInfoServiceImpl.getAllEnableRule'")
    public void delete(Long id) {
        RuleInfo ruleInfo = this.getById(id);
        if (ruleInfo == null) {
            throw new IllegalArgumentException("规则不存在");
        }
        this.removeById(id);
        //如果删除的是精准控制规则，则需要通知网关重新拉取精准匹配规则
        if (RuleTypeEnum.ACCURATE.getValue().equals(ruleInfo.getRuleType()) && BooleanUtils.toBoolean(ruleInfo.getEnable())) {
            sendAccurateRuleConfigUpdateKafkaMessage();
        }
    }

    @Override
    public List<CrawlerAgainstMessageDTO> fetchAccurateRuleConfig() {
        //这里本类调用 不会走缓存
        List<RuleInfoDTO> allRule = getAllEnableRule();
        Date now = new Date();
        List<RuleInfoDTO> accurateRuleList = allRule.stream()
                .filter(dto -> RuleTypeEnum.ACCURATE.equals(dto.getRuleType()))
                .filter(dto -> dto.getEndTime() == null || dto.getEndTime().after(now))
                .collect(Collectors.toList());
        return accurateRuleList.stream().map(rule -> {
            CrawlerAgainstMessageDTO messageDTO = new CrawlerAgainstMessageDTO();
            BeanUtils.copyProperties(rule, messageDTO);
            messageDTO.setMatchUrlList(StringUtils.isBlank(rule.getMatchUrlList()) ? Lists.newArrayList() : Arrays.asList(rule.getMatchUrlList().split(",")));
            messageDTO.setExcludeUrlList(StringUtils.isBlank(rule.getExcludeUrlList()) ? Lists.newArrayList() : Arrays.asList(rule.getExcludeUrlList().split(",")));
            return messageDTO;
        }).collect(Collectors.toList());
    }


    @Override
    public void sendAccurateRuleConfigUpdateKafkaMessage() {
        CrawlerAgainstMessageDTO crawlerAgainstMessageDTO = new CrawlerAgainstMessageDTO();
        crawlerAgainstMessageDTO.setRuleType(RuleTypeEnum.ACCURATE);
        crawlerAgainstMessageDTO.setAccurateRuleConfigList(fetchAccurateRuleConfig());
        String msg = JSON.toJSONString(crawlerAgainstMessageDTO);
        log.info("发送精准匹配规则更新消息:{}", msg);
        kafkaTemplate.send(crawlerAgainstProperties.getActionNotifyKafkaTopic(), msg);
    }
}
