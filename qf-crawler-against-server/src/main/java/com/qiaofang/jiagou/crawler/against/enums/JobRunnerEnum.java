package com.qiaofang.jiagou.crawler.against.enums;

/**
 * job枚举
 *
 * @author shihao.liu
 * @date 2019-6-12
 */
public enum JobRunnerEnum {
    /**
     * job
     */
    RELIEVE_FORBIDDEN_JOB("relieveForbiddenJob", "解除封禁JOB"),
    RULE_END_TIME_HANDLE_JOB("ruleEndTimeHandleJob", "规则截止时间处理JOB");;

    private final String code;
    private final String value;

    JobRunnerEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }


    public String getValue() {
        return value;
    }

    /**
     * getByCode
     * @param code
     * @return
     */
    public static JobRunnerEnum getByCode(String code){
        for (JobRunnerEnum value : JobRunnerEnum.values()) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

}
