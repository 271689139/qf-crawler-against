package com.qiaofang.jiagou.crawler.against.operatelog;

import com.qiaofang.core.operatelog.bean.AddOperlogDTO;
import com.qiaofang.core.operatelog.service.OperatelogSender;
import com.qiaofang.jiagou.innersso.model.UserInfoModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 操作日志组件
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019/11/13 4:37 下午
 */
@Component
public class OperateLogComponent {

    @Resource
    private OperatelogSender operatelogSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final List<String> SENSITIVE_STR_LIST = Collections.singletonList("password");

    /**
     * 发送操作日志
     *
     * @param userInfo
     * @param type
     * @param content
     */
    @Async
    void sendOperateLog(UserInfoModel userInfo, String type, String content) {
        try {
            if (userInfo == null){
                return;
            }
            AddOperlogDTO dto = new AddOperlogDTO();
            dto.setEmployeeUuid(userInfo.getDingTalkUserId());
            dto.setEmployeeName(userInfo.getName());
            dto.setType(type);
            for (String sensitiveStr : SENSITIVE_STR_LIST) {
                content = desensitization(content, sensitiveStr);
            }
            dto.setContent(content);
            dto.setOperateDate(new Date());
            operatelogSender.send(dto);
        } catch (Exception e) {
            logger.error("发生操作日志失败", e);
        }
    }

    /**
     * 脱敏
     *
     * @param content
     * @return
     */
    private String desensitization(String content, String sensitiveStr) {
        if (StringUtils.isBlank(content) || !content.contains(sensitiveStr)) {
            return content;
        }
        String[] ss = content.split(sensitiveStr);
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                target.append(sensitiveStr).append("\":\"******\"").append(ss[i].substring(ss[i].indexOf(',')));
            } else {
                target.append(ss[i]);
            }
        }
        return target.toString();
    }
}
