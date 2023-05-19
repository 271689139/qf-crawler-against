package com.qiaofang.jiagou.crawler.against.stub.enums;

/**
 * 规则类型
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum RuleTypeEnum {

    /**
     * 规则类型 ACCURATE-精准控制 DYNAMIC-动态控制
     */
    ACCURATE("ACCURATE", "精准控制"),
    DYNAMIC("DYNAMIC", "动态控制");


    public static RuleTypeEnum getEnumByValue(String value) {
        for (RuleTypeEnum e : RuleTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    RuleTypeEnum(String value, String desc) {
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

