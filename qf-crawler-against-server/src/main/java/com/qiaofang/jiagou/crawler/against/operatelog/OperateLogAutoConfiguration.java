package com.qiaofang.jiagou.crawler.against.operatelog;

import com.qiaofang.core.operatelog.config.OperatelogProperties;
import com.qiaofang.core.operatelog.service.OperatelogSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OperatelogProperties.class})
public class OperateLogAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
        value = {"operatelog.enabled"},
        havingValue = "true",
        matchIfMissing = true
    )
    public OperatelogSender getOperateLogSender(OperatelogProperties operatelogProperties) {
        operatelogProperties.setSystem("crawleragainst");
        return new OperatelogSender(operatelogProperties.getKafkaServer(), operatelogProperties.getKafkaTopic(), operatelogProperties.getKafkaClientId(), operatelogProperties.getSystem(), operatelogProperties.getProfile());
    }
}
