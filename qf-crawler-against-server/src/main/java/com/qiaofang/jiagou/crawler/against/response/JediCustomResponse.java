package com.qiaofang.jiagou.crawler.against.response;

import com.qiaofang.common.constant.CommonResponseCode;
import com.qiaofang.common.response.DataResultResponse;
import lombok.Data;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/8/14 10:50 上午
 */
@Data
public class JediCustomResponse<T> {

    private String code = "1";

    private T data;

    private DataResultResponse error;


    public static <V> JediCustomResponse<V> fail(String message) {
        DataResultResponse<V> error = new DataResultResponse();
        error.setResponseCode(CommonResponseCode.RC_EXCEPTION_ERROR.getResponseCode());
        error.setResponseMessage(message);
        JediCustomResponse<V> response = new JediCustomResponse<>();
        response.setCode(error.getResponseCode());
        response.setError(error);
        return response;
    }

    public static <V> JediCustomResponse<V> fail(String code, String message) {
        JediCustomResponse<V> response = new JediCustomResponse<>();
        DataResultResponse<V> error = new DataResultResponse();
        error.setResponseCode(code);
        error.setResponseMessage(message);
        response.setCode(error.getResponseCode());
        response.setError(error);
        return response;
    }

    public static <V> JediCustomResponse<V> ok(V data) {
        JediCustomResponse<V> response = new JediCustomResponse<>();
        response.setData(data);
        return response;
    }
}
