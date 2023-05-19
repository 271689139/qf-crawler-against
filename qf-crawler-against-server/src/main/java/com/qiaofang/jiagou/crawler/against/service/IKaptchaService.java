package com.qiaofang.jiagou.crawler.against.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/7/6 3:14 下午
 */
public interface IKaptchaService {

    /**
     * 生成token
     *
     * @return
     */
    String generateToken();

    /**
     * 生成验证码
     *
     * @param token
     * @param width
     * @param height
     * @param validSecond
     * @param request
     * @param response
     */
    void render(String token, String width, String height, Integer validSecond, HttpServletRequest request, HttpServletResponse response);

    /**
     * 验证
     *
     * @param token
     * @param code
     * @param matchDimensionKey
     */
    void valid(String token, String code, String matchDimensionKey);
}
