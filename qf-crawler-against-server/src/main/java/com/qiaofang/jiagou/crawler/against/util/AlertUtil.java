package com.qiaofang.jiagou.crawler.against.util;

import com.qiaofang.common.constant.CommonResponseCode;
import com.qiaofang.common.request.DataRequest;
import com.qiaofang.common.response.CommonResponse;
import com.qiaofang.jiagou.crawler.against.param.AlertSendRobotMessageParam;
import com.qiaofang.jiagou.crawler.against.param.AlertSendWorkMessageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 报警工具类
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 5:02 下午
 */
@Slf4j
public class AlertUtil {

    private final String serverUrl;
    private final String robotAccessToken;

    public AlertUtil(String serverUrl, String robotAccessToken) {
        this.serverUrl = serverUrl;
        this.robotAccessToken = robotAccessToken;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送钉钉工作通知
     *
     * @param title
     * @param text
     * @param userIdList
     */
    public void sendWorkMessage(String title, String text, List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            log.warn("通知人列表为空");
            return;
        }
        try {
            DataRequest<AlertSendWorkMessageParam> request = new DataRequest<>();
            AlertSendWorkMessageParam param = new AlertSendWorkMessageParam();
            param.setText(text);
            param.setTitle(title);
            param.setUseridList(String.join(",", userIdList));
            request.setParam(param);
            String url = serverUrl + "/alert/dingding/sendWorkMessage";
            CommonResponse response = restTemplate.postForObject(url, request, CommonResponse.class);
            if (!CommonResponseCode.RC_SUCCESS.getResponseCode().equals(response.getResponseCode())) {
                log.error("发送工作通知失败，原因:{}", response.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("发送报警异常", e);
        }
    }

    /**
     * 发送机器人消息
     *
     * @param title
     * @param text
     * @param phoneList 需要@的人
     */
    public void sendRobotMessage(String title, String text, List<String> phoneList) {
        try {
            DataRequest<AlertSendRobotMessageParam> request = new DataRequest<>();
            AlertSendRobotMessageParam param = new AlertSendRobotMessageParam();
            param.setText(text);
            param.setTitle(title);
            param.setPhoneList(phoneList);
            param.setAccessToken(robotAccessToken);
            param.setAlertDate(System.currentTimeMillis());
            request.setParam(param);
            String url = serverUrl + "/alert/dingding/sendRobotMessage";
            CommonResponse response = restTemplate.postForObject(url, request, CommonResponse.class);
            if (!CommonResponseCode.RC_SUCCESS.getResponseCode().equals(response.getResponseCode())) {
                log.error("发送工作通知失败，原因:{}", response.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("发送报警异常", e);
        }
    }

}
