package com.qiaofang.jiagou.crawler.against.config;

import com.qiaofang.jiagou.innersso.authentication.HttpSessionRecordListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisSessionListenerConfig extends RedisHttpSessionConfiguration {

    public RedisSessionListenerConfig() {
        List<HttpSessionListener> list = new ArrayList<>();
        list.add(new HttpSessionRecordListener());
        this.setHttpSessionListeners(list);
        this.setMaxInactiveIntervalInSeconds(60);
    }
}