package com.qiaofang.jiagou.crawler.against.stub.enums;

/**
 * 匹配规则处理记录状态
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum MatchRecordStatusEnum {

    /**
     * 匹配规则处理记录状态
     */
    PROCESS(0, "进行中"),
    DONE(1, "已完成");

    public static MatchRecordStatusEnum getEnumByValue(int value) {
        for (MatchRecordStatusEnum e : MatchRecordStatusEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }

    private final int value;

    private final String desc;

    MatchRecordStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }
}

