package com.qiaofang.jiagou.crawler.against.stub.dto;

import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 反爬消息
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 3:54 下午
 */
@Data
public class CrawlerAgainstMessageDTO {


    /**
     * 规则类型 默认为动态匹配
     */
    private RuleTypeEnum ruleType = RuleTypeEnum.DYNAMIC;
    /**
     * 匹配记录id
     */
    private Long matchRecordId;
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 业务线 如果为空则是全局配置
     */
    private String businessLine;
    /**
     * 规则每天生效时间
     */
    private String effectTime;
    /**
     * 规则每天失效时间
     */
    private String invalidTime;
    /**
     * 规则匹配的URL
     */
    private List<String> matchUrlList;
    /**
     * 排除的URL
     */
    private List<String> excludeUrlList;
    /**
     * 匹配维度标识
     */
    private String matchDimensionMark;
    /**
     * 匹配动作
     */
    private MatchActionEnum matchAction;
    /**
     * 当前匹配记录结束时间
     */
    private Date endTime;
    /**
     * 匹配规则
     */
    private List<RuleMatchConfigDTO> ruleMatchConfigDTOList;
    /**
     * 精准规则列表
     */
    private List<CrawlerAgainstMessageDTO> accurateRuleConfigList;

}
