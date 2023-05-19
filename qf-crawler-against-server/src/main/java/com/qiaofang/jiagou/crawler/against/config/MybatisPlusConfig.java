package com.qiaofang.jiagou.crawler.against.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.qiaofang.jiagou.crawler.against.mybatis.interceptor.CommonFieldUpdateInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2019/8/30 3:52 下午
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.qiaofang.jiagou.crawler.against.mapper*")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public CommonFieldUpdateInterceptor operateRecordInterceptor() {
        return new CommonFieldUpdateInterceptor();
    }
}