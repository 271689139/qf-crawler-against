package com.qiaofang.jiagou.crawler.against.param;

import com.qiaofang.jiagou.crawler.against.stub.enums.MatchActionEnum;
import com.qiaofang.jiagou.crawler.against.stub.enums.RuleTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * 添加规则参数
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/26 2:21 下午
 */
@Data
public class RuleListParam {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型 ACCURATE-精准匹配规则 DYNAMIC-动态匹配规则
     */
    private RuleTypeEnum ruleType;

    /**
     * 匹配动作 FORBIDDEN-封禁 WARNING-报警 PASS-放行 VERIFICATION-需要验证
     */
    private MatchActionEnum matchAction;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 来源
     */
    private List<String> originList;

}
