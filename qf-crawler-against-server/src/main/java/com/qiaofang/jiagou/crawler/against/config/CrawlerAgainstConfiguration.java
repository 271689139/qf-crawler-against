package com.qiaofang.jiagou.crawler.against.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 4:50 下午
 */
@Data
@Configuration
@EnableConfigurationProperties(CrawlerAgainstProperties.class)
public class CrawlerAgainstConfiguration {
}
