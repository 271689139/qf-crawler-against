package com.qiaofang.jiagou.crawler.against.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 匹配规则处理记录表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MatchRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * rule_id
     */
    private Long ruleId;

    /**
     * 匹配动作FORBIDDEN-封禁 WARNING-报警 VERIFICATION-需要验证
     */
    private String matchAction;

    /**
     * 匹配维度标识
     */
    private String matchDimensionMark;

    /**
     * 匹配维度key
     */
    private String matchDimensionKey;

    /**
     * 计数维度标识
     */
    private String tallyDimensionMark;

    /**
     * 匹配规则快照
     */
    private String matchConfigSnapshot;

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
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 状态 0-进行中 1-已完成
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

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
