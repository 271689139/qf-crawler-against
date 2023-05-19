package com.qiaofang.jiagou.crawler.against.param;

import lombok.Data;

import java.util.List;

/**
 * 钉钉机器人消息
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 5:04 下午
 */
@Data
public class AlertSendRobotMessageParam {

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String text;
    /**
     * 机器人token
     */
    private String accessToken;
    /**
     * 需要@的员工的手机号
     */
    private List<String> phoneList;

    /**
     * 报警时间-时间戳
     */
    private Long alertDate;
}
