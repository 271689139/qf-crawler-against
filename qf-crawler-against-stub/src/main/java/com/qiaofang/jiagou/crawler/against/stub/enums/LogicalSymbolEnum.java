package com.qiaofang.jiagou.crawler.against.stub.enums;

/**
 * 逻辑符号
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum LogicalSymbolEnum {

    /**
     * 逻辑符号
     */
    MATCH("MATCH", "匹配"),
    NOT_MATCH("NOT_MATCH", "不匹配"),
    CONTAIN("CONTAIN", "包含"),
    NOT_CONTAIN("NOT_CONTAIN", "不包含"),
    EQUALS("EQUALS","等于"),
    NOT_EQUALS("NOT_EQUALS","不等于"),
    BELONG("BELONG", "属于"),
    NOT_BELONG("NOT_BELONG", "不属于");

    public static LogicalSymbolEnum getEnumByValue(String value) {
        for (LogicalSymbolEnum e : LogicalSymbolEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    LogicalSymbolEnum(String value, String desc) {
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

