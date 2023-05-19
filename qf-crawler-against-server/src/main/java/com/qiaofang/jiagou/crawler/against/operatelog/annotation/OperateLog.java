package com.qiaofang.jiagou.crawler.against.operatelog.annotation;


import java.lang.annotation.*;

/**
 * 需要接入操作日志的注解
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/20 11:34 上午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 对应操作日志的type，如果不写默认使用接口名称.方法名
     *
     * @return
     */
    String type() default "";

    /**
     * 是否包含接口返回数据 默认包含
     *
     * @return
     */
    boolean needResponse() default true;
}
