package com.qiaofang.jiagou.crawler.against.stub.enums;

/**
 * 匹配动作
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum MatchActionEnum {

    /**
     * 匹配动作 FORBIDDEN-封禁 WARNING-报警 PASS-放行 VERIFICATION-验证码
     */
    FORBIDDEN("FORBIDDEN", "封禁"),
    WARNING("WARNING", "报警"),
    PASS("PASS", "放行"),
    VERIFICATION("VERIFICATION", "验证码");


    public static MatchActionEnum getEnumByValue(String value) {
        for (MatchActionEnum e : MatchActionEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    MatchActionEnum(String value, String desc) {
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

