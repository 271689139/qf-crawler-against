package com.qiaofang.jiagou.crawler.against.stub.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 请求日志消息体
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/15 2:32 下午
 */
@Data
public class HttpRequestLogMessageDTO {

    /**
     * 请求url（完整url）
     */
    private String url;
    /**
     * 路径 /api开头
     */
    private String path;
    /**
     * 请求
     */
    private String method;
    /**
     * 公司uuid
     */
    private String companyUuid;
    /**
     * userId
     */
    private String userId;
    /**
     * userUuid
     */
    private String userUuid;
    /**
     * 请求时间
     */
    private Date requestTime;
    /**
     * 请求来源ip
     */
    private String originIp;
    /**
     * 参数
     */
    private Map<String, String> params;
    /**
     * 请求头
     */
    private Map<String, String> headers;

}
