package com.qiaofang.jiagou.crawler.against.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 4:50 下午
 */
@Data
@ConfigurationProperties(prefix = "crawler-against")
public class CrawlerAgainstProperties {

    /**
     * 动作通知
     */
    private String actionNotifyKafkaTopic;
}
