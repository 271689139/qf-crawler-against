package com.qiaofang.jiagou.crawler.against.operatelog;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.qiaofang.jiagou.crawler.against.operatelog.annotation.OperateLog;
import com.qiaofang.jiagou.innersso.model.UserInfoModel;
import com.qiaofang.jiagou.innersso.principal.UserInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * AOP保存操作日志
 *
 * @author shihao.liu
 * @version 1.0
 * @date 17/2/8 下午12:11
 */
@Aspect
@Component
public class OperateLogAspect {

    @Autowired
    private OperateLogComponent operateLogComponent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 切加了注解的方法
     */
    @Pointcut("@annotation(com.qiaofang.jiagou.crawler.against.operatelog.annotation.OperateLog)")
    public void operateLogAspect() {
        //Pointcut
    }

    @Around("operateLogAspect()")
    public Object doAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            MethodSignature methodSignature = (MethodSignature) (joinPoint.getSignature());
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
            Object[] args = joinPoint.getArgs();
            List<Object> params = Lists.newArrayList();
            for (Object arg : args) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                    continue;
                }
                params.add(arg);
            }
            OperateLog operateLog = method.getAnnotation(OperateLog.class);
            if (!operateLog.needResponse()) {
                result = null;
            }
            OperateInfoDTO operateInfoDTO = new OperateInfoDTO(params, result);
            UserInfoModel userInfo = UserInfoHolder.getUserInfo();
            String type = StringUtils.isNotBlank(operateLog.type()) ? operateLog.type() : joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
            operateLogComponent.sendOperateLog(userInfo, type, JSON.toJSONString(operateInfoDTO));
        } catch (Exception e) {
            logger.error("处理操作日志异常", e);
        }
        return result;
    }


}

