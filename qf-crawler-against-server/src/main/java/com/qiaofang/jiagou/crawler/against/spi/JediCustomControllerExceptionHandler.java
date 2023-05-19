package com.qiaofang.jiagou.crawler.against.spi;

import com.alibaba.fastjson.JSON;
import com.qiaofang.common.constant.CommonResponseCode;
import com.qiaofang.common.exception.BusinessException;
import com.qiaofang.jiagou.crawler.against.enums.ResponseCodeEnum;
import com.qiaofang.jiagou.crawler.against.exception.NoRightException;
import com.qiaofang.jiagou.crawler.against.response.JediCustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;


/**
 * @author shihao.liu
 * @version 1.0
 * @date 2019-08-12 11:41
 */
@Slf4j
public class JediCustomControllerExceptionHandler {

    /**
     * 统一异常处理
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler
    public JediCustomResponse<Object> exp(HttpServletRequest request, Exception e) {
        String paramMap = JSON.toJSONString(request.getParameterMap());
        if (e instanceof IllegalArgumentException || e instanceof MissingServletRequestParameterException) {
            return JediCustomResponse.fail(ResponseCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
        }
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : errors) {
                builder.append(error.getDefaultMessage()).append(" ");
            }
            return JediCustomResponse.fail(ResponseCodeEnum.PARAM_ERROR.getCode(), builder.toString());
        }
        if (e instanceof NoRightException) {
            String message = StringUtils.isBlank(e.getMessage()) ? CommonResponseCode.RC_NO_PERMISSION_ERROR.getResponseMessage() : e.getMessage();
            return JediCustomResponse.fail(CommonResponseCode.RC_NO_PERMISSION_ERROR.getResponseCode(), message);
        }
        if (e instanceof BusinessException) {
            String errorCode = ((BusinessException) e).getErrorCode();
            if (errorCode != null){
                return JediCustomResponse.fail(errorCode, e.getMessage());
            }
            return JediCustomResponse.fail(ResponseCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        log.error("exception occurred：{},params:{}", e.getMessage(), paramMap, e);
        if (e instanceof MyBatisSystemException || e instanceof PersistenceException || e instanceof SQLException) {
            return JediCustomResponse.fail(ResponseCodeEnum.SYSTEM_ERROR.getCode(), "SQL执行异常，请联系管理员");
        }
        if (e instanceof RuntimeException) {
            return JediCustomResponse.fail(ResponseCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        return JediCustomResponse.fail(ResponseCodeEnum.SYSTEM_ERROR.getCode(), ResponseCodeEnum.SYSTEM_ERROR.getMsg());
    }


}
