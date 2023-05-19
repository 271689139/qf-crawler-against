package com.qiaofang.jiagou.crawler.against.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Data
@Configuration
@Slf4j
public class KafkaConfiguration {

    private final KafkaProperties properties;

    public KafkaConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ConsumerFactory kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory(this.properties.buildConsumerProperties()){
            @Override
            public Consumer createConsumer() {
                return super.createKafkaConsumer();
            }

            @Override
            public Consumer createConsumer(String clientIdSuffix) {
                return super.createKafkaConsumer(null, null);
            }

            @Override
            public Consumer createConsumer(String groupId, String clientIdSuffix) {
                return super.createKafkaConsumer(groupId, null);
            }

        };
    }
}
