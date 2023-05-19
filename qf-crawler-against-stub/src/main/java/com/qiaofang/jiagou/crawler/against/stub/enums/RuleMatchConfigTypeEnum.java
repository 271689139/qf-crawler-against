package com.qiaofang.jiagou.crawler.against.stub.enums;

/**
 * 规则匹配配置类型
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum RuleMatchConfigTypeEnum {

    /**
     * 规则匹配配置类型
     */
    MATCH_RULE("MATCH_RULE", "匹配规则"),
    MATCH_DIMENSION("MATCH_DIMENSION", "匹配维度");


    public static RuleMatchConfigTypeEnum getEnumByValue(String value) {
        for (RuleMatchConfigTypeEnum e : RuleMatchConfigTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    RuleMatchConfigTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getValue() {
        return value;
    }
}

