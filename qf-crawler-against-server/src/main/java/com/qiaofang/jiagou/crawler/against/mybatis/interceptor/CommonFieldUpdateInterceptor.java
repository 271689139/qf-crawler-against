package com.qiaofang.jiagou.crawler.against.mybatis.interceptor;

import com.qiaofang.jiagou.crawler.against.entity.BaseEntity;
import com.qiaofang.jiagou.innersso.model.UserInfoModel;
import com.qiaofang.jiagou.innersso.principal.UserInfoHolder;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Intercepts设置拦截的目标对象为 Executor的update方法
 *
 * @author shihao.liu
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class CommonFieldUpdateInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(CommonFieldUpdateInterceptor.class);



    /**
     * 执行更新的六个字段名
     */
    private final static String FIELD_CREATE_TIME = "createTime";
    private final static String FIELD_UPDATE_TIME = "updateTime";
    private final static String FIELD_CREATE_NAME = "createUser";
    private final static String FIELD_UPDATE_NAME = "updateUser";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            //获取当前操作的用户
            UserInfoModel userInfo = UserInfoHolder.getUserInfo();

            //第一个参数为MappedStatement， Executor的update方法的内置参数
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

            //第二个参数才是我们需要的处理的数据库Entity对象，比如通过updateByExample(parameter)中的参数
            Object parameter = invocation.getArgs()[1];

            if (parameter instanceof MapperMethod.ParamMap) {
                for (Object arg : ((Map<?, ?>) parameter).values()) {
                    if (arg instanceof BaseEntity) {
                        parameter = arg;
                        break;
                    }
                }
            }else if (!(parameter instanceof BaseEntity)){
                return invocation.proceed();
            }
            //得到sql类型
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

            Date date = new Date();
            //SQL插入动作
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                //插入数据库时设置六个字段
                setOperateTime(parameter, FIELD_CREATE_TIME, date);
                setOperateUser(parameter, FIELD_CREATE_NAME, userInfo);
                setOperateTime(parameter, FIELD_UPDATE_TIME, date);
                setOperateUser(parameter, FIELD_UPDATE_NAME, userInfo);
            }
            //SQL更新动作
            else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                //更新数据库时设置后三个字段
                setOperateTime(parameter, FIELD_UPDATE_TIME, date);
                setOperateUser(parameter, FIELD_UPDATE_NAME, userInfo);
            }
        } catch (Exception e) {
            //这里需要捕获异常，为了保证主流程不会异常阻断
            log.error("Mybatis OperateRecordInterceptor 拦截器异常", e);
        }

        //继续执行该执行的方法
        return invocation.proceed();
    }


    private void setOperateTime(Object parameter, String operateTimeFieldName, Date date) throws IllegalAccessException {
        try {
            Field field = getField(parameter, operateTimeFieldName);
            setFieldValue(field, parameter, date);
        } catch (Exception e) {
            log.info("setOperateTime error, entity:{}, operateTimeFieldName:{}", parameter.getClass().getName(), operateTimeFieldName);
        }
    }

    private void setOperateUser(Object parameter, String operateFieldName, UserInfoModel userInfoModel) throws IllegalAccessException {
        try {
            if (userInfoModel == null){
                return;
            }
            Field field = getField(parameter, operateFieldName);
            Object value = userInfoModel.getDingTalkUserId();
            setFieldValue(field, parameter, value);
        } catch (Exception e) {
            log.info("setOperateUser error, entity:{}, operateFieldName:{}", parameter.getClass().getName(), operateFieldName);
        }
    }

    private void setFieldValue(Field field, Object obj, Object value) throws IllegalAccessException {
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    private Field getField(Object object, String fieldName) {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.error("从[{}]中获取[{}]字段发生异常：{}:{}", object.getClass().getName(), fieldName, e.getClass().getName(), e.getMessage());
        }
        return field;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //nothing
    }
}