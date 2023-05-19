package com.qiaofang.jiagou.crawler.against;

import com.github.ltsopensource.spring.boot.annotation.EnableTaskTracker;
import com.qiaofang.core.cacheclient.annotation.EnableCoreCache;
import com.qiaofang.core.cacheclient.redisson.annotation.EnableCoredisson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/13 3:54 下午
 */
@EnableCoreCache
@EnableCoredisson
@EnableTaskTracker
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 6 * 60 * 60)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
