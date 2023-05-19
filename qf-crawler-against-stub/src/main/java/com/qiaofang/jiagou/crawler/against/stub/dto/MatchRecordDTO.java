package com.qiaofang.jiagou.crawler.against.stub.dto;

import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 匹配规则处理记录表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
public class MatchRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * rule_id
     */
    private Long ruleId;
    /**
     * 匹配动作 FORBIDDEN-封禁 WARNING-报警 VERIFICATION-需要验证
     */
    private MatchActionEnum matchAction;
    /**
     * 匹配动作描述
     */
    private String matchActionDesc;
    /**
     * 匹配计数维度标识
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
     * 规则匹配的URL
     */
    private List<String> matchUrlList;

    /**
     * 排除的URL
     */
    private List<String> excludeUrlList;

    /**
     * 匹配规则
     */
    private List<RuleMatchConfigDTO> ruleMatchConfigDTOList;

}
