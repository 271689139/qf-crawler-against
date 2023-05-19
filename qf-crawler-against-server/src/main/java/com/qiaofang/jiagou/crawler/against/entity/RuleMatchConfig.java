package com.qiaofang.jiagou.crawler.against.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 规则匹配配置表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RuleMatchConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * rule_id
     */
    private Long ruleId;

    /**
     * 匹配条件 IP URL PARAM METHOD HEADER
     */
    private String matchCondition;

    /**
     * 匹配条件详细，目前只有match_condition=HEADER时该字段才有值
     */
    private String matchConditionDetail;

    /**
     * 逻辑符号
     */
    private String logicalSymbol;

    /**
     * 匹配内容
     */
    private String matchContent;

    /**
     * 是否作为封禁维度（动态规则属性）
     */
    private Boolean forbiddenFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 修改者
     */
    private String updateUser;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;


}
