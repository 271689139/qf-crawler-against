package com.qiaofang.jiagou.crawler.against.enums;

/**
 * 规则来源
 * @author shihao.liu
 * @version 1.0
 * @date 2020/7/14 4:18 下午
 */
public enum RuleOriginEnum {


    /**
     * 来源 MANUAL-手动添加 SYSTEM_TEMP-系统临时
     */
    MANUAL("MANUAL", "手动添加"),
    SYSTEM_TEMP("SYSTEM_TEMP", "系统临时添加");


    public static RuleOriginEnum getEnumByValue(String value) {
        for (RuleOriginEnum e : RuleOriginEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    RuleOriginEnum(String value, String desc) {
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
