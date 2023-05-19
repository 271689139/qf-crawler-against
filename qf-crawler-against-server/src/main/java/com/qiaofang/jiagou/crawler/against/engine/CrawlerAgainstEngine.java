package com.qiaofang.jiagou.crawler.against.engine;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.qiaofang.jiagou.crawler.against.constant.RedisKeyConstant;
import com.qiaofang.jiagou.crawler.against.dto.RuleInfoDTO;
import com.qiaofang.jiagou.crawler.against.service.IMatchRecordService;
import com.qiaofang.jiagou.crawler.against.service.IRuleInfoService;
import com.qiaofang.jiagou.crawler.against.stub.dto.HttpRequestLogMessageDTO;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import com.qiaofang.jiagou.crawler.against.stub.util.RequestMatchUtil;
import com.qiaofang.jiagou.crawler.against.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 反爬引擎
 * 计数、封禁
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/15 2:19 下午
 */
@Slf4j
@Component
public class CrawlerAgainstEngine {


    private static IRuleInfoService ruleInfoService;
    @Autowired
    public void setRuleInfoService(IRuleInfoService ruleInfoService) {
        CrawlerAgainstEngine.ruleInfoService = ruleInfoService;
    }

    @Autowired
    private IMatchRecordService matchRecordService;
    @Autowired
    private RedisUtil redisUtil;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final static String ALL_RULE_KEY = "RuleInfoServiceImpl.getAllEnableRule";
    private static LoadingCache<String, List<RuleInfoDTO>> cache = CacheBuilder.newBuilder()
            .maximumSize(5)  //最多存放数据
            .expireAfterWrite(5, TimeUnit.MINUTES)  //存放5分钟
            .build(new CacheLoader<String, List<RuleInfoDTO>>(){
                @Override
                public List<RuleInfoDTO> load(String key) throws Exception {
                    return ruleInfoService.getAllEnableRule();
                }
            });

    /**
     * 根据请求日志匹配规则 并根据配置做对应处理
     *
     * @param messageDTO 消息体
     */
    public void handle(HttpRequestLogMessageDTO messageDTO) {
        String path = UriComponentsBuilder.fromUriString(messageDTO.getUrl()).build().getPath();

        List<RuleInfoDTO> allRule = null;
        try {
            allRule = cache.get(ALL_RULE_KEY);
        } catch (Exception e) {
            log.warn("getAllEnableRule cache error!", e);
            allRule = Lists.newArrayList();
        }

        //理论上符合精准匹配规则的请求不应该走到这里，这里只做报警
        List<RuleInfoDTO> accurateRuleList = allRule.stream()
                .filter(dto -> RuleTypeEnum.ACCURATE.equals(dto.getRuleType()))
                .filter(dto -> inTimeScope(dto.getEffectTime(), dto.getInvalidTime()))
                .filter(dto -> dto.getEndTime() == null || dto.getEndTime().after(new Date()))
                .collect(Collectors.toList());
        for (RuleInfoDTO ruleInfoDTO : accurateRuleList) {
            if (matchPath(ruleInfoDTO.getMatchUrlList(), path) && excludePath(ruleInfoDTO.getExcludeUrlList(), path) && RequestMatchUtil.match(messageDTO, ruleInfoDTO.getRuleMatchConfigDTOList())) {
                log.error("精准匹配规则没有生效, ruleId:{}", ruleInfoDTO.getId());
                ruleInfoService.sendAccurateRuleConfigUpdateKafkaMessage();
                return;
            }
        }
        //再处理动态匹配
        List<RuleInfoDTO> dynamicRuleList = allRule.stream()
                .filter(ruleInfoDTO -> RuleTypeEnum.DYNAMIC.equals(ruleInfoDTO.getRuleType()))
                .filter(ruleInfoDTO -> inTimeScope(ruleInfoDTO.getEffectTime(), ruleInfoDTO.getInvalidTime()))
                .collect(Collectors.toList());
        dynamicRuleList.stream().filter(ruleInfoDTO -> matchPath(ruleInfoDTO.getMatchUrlList(), path) && excludePath(ruleInfoDTO.getExcludeUrlList(), path))
                .forEach(ruleInfoDTO -> tally(ruleInfoDTO, messageDTO));
    }

    /**
     * 计数
     *
     * @param ruleInfoDTO 规则
     * @param messageDTO  请求信息
     */
    private void tally(RuleInfoDTO ruleInfoDTO, HttpRequestLogMessageDTO messageDTO) {
        try {
            //匹配规则
            List<RuleMatchConfigDTO> accurateRuleMatchConfigDTOList = ruleInfoDTO.getAccurateRuleMatchConfigDTOList();
            if (!RequestMatchUtil.match(messageDTO, accurateRuleMatchConfigDTOList)) {
                return;
            }
            //计数维度
            List<RuleMatchConfigDTO> tallDimensionConfigDTOList = ruleInfoDTO.getTallDimensionConfigDTOList();
            String tallyDimensionMark = RequestMatchUtil.assembleMatchDimensionMark(ruleInfoDTO.getId(), tallDimensionConfigDTOList, messageDTO);
            if (StringUtils.isBlank(tallyDimensionMark)) {
                return;
            }
            String redisKey = RedisKeyConstant.REQUEST_TIMES + DigestUtils.md5Hex(tallyDimensionMark);
            Long requestCount = redisUtil.increment(redisKey, 1);
            //如果计数为1 说明是刚生成的key，设置当前key 过期时间为配置的检测时长
            if (requestCount == 1) {
                redisUtil.expire(redisKey, ruleInfoDTO.getDetectionDuration(), TimeUnit.MINUTES);
            }
            if (requestCount > ruleInfoDTO.getLimitRequestTimes()) {
                redisUtil.delete(redisKey);
                matchRecordService.createRecord(ruleInfoDTO, messageDTO, tallyDimensionMark);
            }
        } catch (Exception e) {
            String ruleInfoStr = JSON.toJSONString(ruleInfoDTO);
            String messageStr = JSON.toJSONString(messageDTO);
            log.error("处理计数发生异常,ruleInfoDTO:{}, messageDTO:{}", ruleInfoStr, messageStr, e);
        }
    }


    /**
     * path是否匹配
     *
     * @param matchUrlList
     * @param path
     * @return
     */
    private boolean matchPath(String matchUrlList, String path) {
        if (matchUrlList == null || matchUrlList.isEmpty()) {
            return true;
        }
        //检查URL是否匹配
        for (String url : matchUrlList.split(",")) {
            if (pathMatcher.match(url, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * path是否在排除的url之外
     *
     * @param excludeUrlList
     * @param path
     * @return
     */
    private boolean excludePath(String excludeUrlList, String path) {
        if (excludeUrlList == null || excludeUrlList.isEmpty()) {
            return true;
        }
        //检查URL是否匹配,这里如果匹配上了 如果当前path不在排除的url之外，返回false
        for (String url : excludeUrlList.split(",")) {
            if (pathMatcher.match(url, path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前是否在指定时间范围外
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    private boolean inTimeScope(String beginTime, String endTime) {
        try {
            if (StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)) {
                return true;
            }
            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTime(df.parse(df.format(new Date())));

            Calendar begin = Calendar.getInstance();
            begin.setTime(df.parse(beginTime));

            Calendar end = Calendar.getInstance();
            end.setTime(df.parse(endTime));

            return date.after(begin) && date.before(end);
        } catch (Exception e) {
            log.error("判断是否在时间范围发生异常,beginTime:{}, endTime:{}", beginTime, endTime, e);
            return true;
        }
    }

}
