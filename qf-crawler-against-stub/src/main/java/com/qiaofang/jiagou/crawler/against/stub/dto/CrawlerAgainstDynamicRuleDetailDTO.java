package com.qiaofang.jiagou.crawler.against.stub.dto;

import com.qiaofang.jiagou.crawler.against.stub.enums.MatchConditionEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 反爬动态规则详细信息
 * 存储每个规则下面被限制的请求信息
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 3:54 下午
 */
@Data
public class CrawlerAgainstDynamicRuleDetailDTO {


    private Long ruleId;
    /**
     * 匹配条件
     */
    private List<MatchConditionEnum> conditionList;
    /**
     * 匹配hash
     * key为CrawlerAgainstMessageDTO中matchDimensionMark的MD5值
     */
    private Map<String, CrawlerAgainstMessageDTO> matchMap;

}
