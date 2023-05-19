package com.qiaofang.jiagou.crawler.against.constant;

/**
 * redis key 定义
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/15 5:46 下午
 */
public class RedisKeyConstant {

    /**
     * 计数
     */
    public static final String REQUEST_TIMES = "request_times:";
    /**
     * 分布式锁
     */
    public static final String LOCK = "lock:";
    /**
     * 报警限制
     */
    public static final String WARNING_LIMIT = "warning_limit:";
    /**
     * 动作通知限制
     */
    public static final String ACTION_NOTIFY_LIMIT = "action_notify_limit:";
    /**
     * 触发验证码的匹配记录
     */
    public static final String VERIFICATION_RECORD = "tortoisegateway_verification_record:";
    /**
     * 验证码
     */
    public static final String CAPTCHA = "captcha:";
    /**
     * 验证码
     */
    public static final String CAPTCHA_TOKEN = "captcha_token:";

}
