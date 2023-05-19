package com.qiaofang.jiagou.crawler.against.kafka;

import com.alibaba.fastjson.JSON;
import com.qiaofang.jiagou.crawler.against.engine.CrawlerAgainstEngine;
import com.qiaofang.jiagou.crawler.against.stub.dto.HttpRequestLogMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author shihao.liu
 */
@Slf4j
@Component
public class HttpRequestMessageListener {

    @Autowired
    private CrawlerAgainstEngine crawlerAgainstEngine;

    @KafkaListener(topics = "${crawler-against.http-request-kafka-topic:gateway-log}")
    public void onMessage(String msg) {
        try {
            //LogContext.getCurrent().setTraceId(StringUtil.getString(TracerUtils.getTracerId()));
            log.trace("[request log],msg: {}", msg);
            HttpRequestLogMessageDTO messageDTO = JSON.parseObject(msg, HttpRequestLogMessageDTO.class);
            crawlerAgainstEngine.handle(messageDTO);
        } catch (Exception e) {
            log.error("处理网关请求日志失败, msg:{}", msg, e);
        }
    }
}