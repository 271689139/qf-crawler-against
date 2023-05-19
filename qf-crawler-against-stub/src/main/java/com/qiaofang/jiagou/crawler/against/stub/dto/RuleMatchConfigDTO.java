package com.qiaofang.jiagou.crawler.against.stub.dto;

import com.qiaofang.jiagou.crawler.against.stub.enums.LogicalSymbolEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchConditionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleMatchConfigTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 规则匹配配置表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
public class RuleMatchConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * rule_id
     */
    private Long ruleId;

    /**
     * 匹配条件 IP URL PARAM METHOD HEADER
     */
    private MatchConditionEnum matchCondition;

    /**
     * 匹配条件详细，目前只有match_condition=HEADER时该字段才有值
     */
    private String matchConditionDetail;

    /**
     * 逻辑符号
     */
    private LogicalSymbolEnum logicalSymbol;

    /**
     * 匹配内容
     */
    private String matchContent;

    /**
     * 规则匹配配置类型
     */
    private RuleMatchConfigTypeEnum ruleMatchConfigTypeEnum;

    /**
     * 是否作为封禁维度（动态规则属性）
     */
    private Boolean forbiddenFlag;


}
