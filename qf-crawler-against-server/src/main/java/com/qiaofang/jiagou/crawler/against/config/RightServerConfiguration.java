package com.qiaofang.jiagou.crawler.against.config;

import com.qiaofang.jiagou.crawler.against.util.UserUtil;
import com.qiaofang.jiagou.innersso.config.InnerSsoProperties;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2020/4/20 4:50 下午
 */
@Data
@Configuration
public class RightServerConfiguration {

    @Resource
    private InnerSsoProperties innerSsoProperties;

    @Bean
    public UserUtil userUtil() {
        return new UserUtil(innerSsoProperties.getRightServerUrlPrefix());
    }

}
