package com.qiaofang.jiagou.crawler.against.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaofang.jiagou.crawler.against.stub.dto.RuleMatchConfigDTO;
import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 添加规则参数
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/26 2:21 下午
 */
@Data
public class RuleCreateOrUpdateParam {

    private Long id;
    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 规则类型 ACCURATE-精准匹配规则 DYNAMIC-动态匹配规则
     */
    @NotNull(message = "规则类型不能为空")
    private RuleTypeEnum ruleType;

    /**
     * 匹配动作 FORBIDDEN-封禁 WARNING-报警 PASS-放行 VERIFICATION-需要验证
     */
    @NotNull(message = "匹配动作不能为空")
    private MatchActionEnum matchAction;

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
    @Max(value = 10000,message = "检测频率不能超过10000分钟")
    private Integer detectionDuration;

    /**
     * 限制请求次数
     */
    @Max(value = 20000000,message = "限制请求次数不能超过20000000")
    private Integer limitRequestTimes;

    /**
     * 封禁时长
     */
    @Max(value = 1000000,message = "封禁时长不能超过1000000分钟")
    private Integer forbiddenDuration;

    /**
     * 匹配规则
     */
    @NotNull(message = "匹配规则不能为空")
    @Size(min = 1, message = "至少需要一条匹配规则")
    private List<RuleMatchConfigDTO> ruleMatchConfigDTOList;

    /**
     * 报警人列表
     */
    private List<String> warningUserIdList;

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

    private Boolean enable;
    /**
     * 规则的截止时间 是一个固定的时间点 与invalid_time不同
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}
