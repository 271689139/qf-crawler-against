package com.qiaofang.jiagou.crawler.against.config;

import com.qiaofang.jiagou.crawler.against.util.AlertUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 4:50 下午
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alert")
public class AlertConfiguration {

    private String serverUrl;

    /**
     * 报警机器人AccessToken
     */
    private String robotAccessToken;


    @Bean
    public AlertUtil alertUtil() {
        return new AlertUtil(serverUrl, robotAccessToken);
    }

}
