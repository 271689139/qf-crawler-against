package com.qiaofang.jiagou.crawler.against.dto;

import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 反爬规则表
 * </p>
 *
 * @author shihao.liu
 * @since 2020-04-13
 */
@Data
public class RuleInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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
    private RuleTypeEnum ruleType;
    private String ruleTypeDesc;
    /**
     * 匹配动作FORBIDDEN-封禁 WARNING-报警 PASS-放行 VERIFICATION-需要验证
     */
    private MatchActionEnum matchAction;
    private String matchActionDesc;

    /**
     * 匹配URL
     */
    private String matchUrlList;

    /**
     * 排除的URL
     */
    private String excludeUrlList;

    /**
     * 检测频率
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
     * 匹配规则-所有
     */
    private List<RuleMatchConfigDTO> ruleMatchConfigDTOList;
    /**
     * 精准匹配规则
     */
    private List<RuleMatchConfigDTO> accurateRuleMatchConfigDTOList;
    /**
     * 计数维度
     */
    private List<RuleMatchConfigDTO> tallDimensionConfigDTOList;
    /**
     * 封禁维度
     */
    private List<RuleMatchConfigDTO> forbiddenDimensionConfigDTOList;
    /**
     * 匹配规则描述
     */
    private String ruleMatchConfigDesc;
    /**
     * 报警人userId列表
     */
    private List<String> warningUserIdList;
    /**
     * 对应报警人信息
     */
    private List<UserInfoDTO> userList;
    /**
     * 优先级，越大越靠前
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
     * 修改时间
     */
    private Date updateTime;
}
