package com.qiaofang.jiagou.crawler.against.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 反爬规则表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RuleInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 业务线 如果为空则是全局配置
     */
    private String businessLine;
    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型 ACCURATE-精准匹配规则 DYNAMIC-动态匹配规则
     */
    private String ruleType;

    /**
     * 匹配动作FORBIDDEN-封禁 WARNING-报警 PASS-放行 VERIFICATION-需要验证
     */
    private String matchAction;

    /**
     * 匹配URL
     */
    private String matchUrlList;

    /**
     * 排除的URL
     */
    private String excludeUrlList;

    /**
     * 检测时长
     */
    private Integer detectionDuration;

    /**
     * 限制请求次数
     */
    private Integer limitRequestTimes;

    /**
     * 封禁时长
     */
    private Integer forbiddenDuration;

    /**
     * 报警人列表 用逗号隔开
     */
    private String warningUserIdList;

    /**
     * 排序编号，越大越靠前
     */
    private Integer sortNo;

    /**
     * 规则每天生效时间
     */
    private String effectTime;

    /**
     * 规则每天失效时间
     */
    private String invalidTime;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 规则的截止时间 是一个固定的时间点 与invalid_time不同
     */
    private Date endTime;

    /**
     * 来源 MANUAL-手动添加 SYSTEM_TEMP-系统临时
     */
    private String origin;

    /**
     * 修改者
     */
    private String updateUserName;

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
