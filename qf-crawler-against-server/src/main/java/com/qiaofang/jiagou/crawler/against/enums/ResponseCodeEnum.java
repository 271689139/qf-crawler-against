package com.qiaofang.jiagou.crawler.against.enums;

/**
 * @author shihao.liu
 */

public enum ResponseCodeEnum {
    /**
     * 常用返回码
     */
    OK("ok", "成功"),
    NO_LOGIN("no_login", "未登录"),
    AUTH_FAIL("auth_fail", "验证失败"),
    CAPTCHA_EXPIRE("captcha_expire", "验证码已过期，请重新获取"),
    CAPTCHA_WRONG("captcha_wrong", "验证码错误"),
    CAPTCHA_NOT_FOUND("captcha_not_found", "请先获取验证码"),
    SYSTEM_ERROR("system_error", "系统错误"),
    PARAM_ERROR("param_error", "参数错误");

    private final String code;
    private final String msg;

    ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
