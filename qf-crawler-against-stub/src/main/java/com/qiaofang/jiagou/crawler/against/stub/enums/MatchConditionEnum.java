package com.qiaofang.jiagou.crawler.against.stub.enums;

import java.util.Arrays;
import java.util.List;

import static com.qiaofang.jiagou.crawler.against.stub.enums.LogicalSymbolEnum.*;

/**
 * 匹配条件
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/6 4:56 下午
 */
public enum MatchConditionEnum {

    /**
     * 匹配条件 匹配条件 IP URL PARAM METHOD HEADER
     */
    IP("IP", "IP", Arrays.asList(CONTAIN, NOT_CONTAIN, EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    HEADER("HEADER", "请求头", Arrays.asList(CONTAIN, NOT_CONTAIN, EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    PATH("PATH", "PATH", Arrays.asList(CONTAIN, NOT_CONTAIN, EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    PARAM("PARAM", "参数", Arrays.asList(CONTAIN, NOT_CONTAIN, EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    HTTP_METHOD("HTTP_METHOD", "请求方法", Arrays.asList(EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    USER_ID("USER_ID", "USER_ID", Arrays.asList(EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    USER_UUID("USER_UUID", "USER_UUID", Arrays.asList(EQUALS, NOT_EQUALS, BELONG, NOT_BELONG)),
    COMPANY_UUID("COMPANY_UUID", "COMPANY_UUID", Arrays.asList(EQUALS, NOT_EQUALS, BELONG, NOT_BELONG));


    public static MatchConditionEnum getEnumByValue(String value) {
        for (MatchConditionEnum e : MatchConditionEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    private final String value;

    private final String desc;

    /**
     * 适用的逻辑符号
     */
    private List<LogicalSymbolEnum> logicalSymbolList;

    MatchConditionEnum(String value, String desc, List<LogicalSymbolEnum> logicalSymbolList) {
        this.value = value;
        this.desc = desc;
        this.logicalSymbolList = logicalSymbolList;
    }

    public List<LogicalSymbolEnum> getLogicalSymbolList() {
        return logicalSymbolList;
    }

    public String getDesc() {
        return desc;
    }

    public String getValue() {
        return value;
    }
}

