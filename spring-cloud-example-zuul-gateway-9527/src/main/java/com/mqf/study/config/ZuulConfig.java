package com.mqf.study.config;

import com.mqf.study.filter.AccessFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ZuulConfig
 * @Description TODO
 * @Author mqf
 * @Date 2019/5/9 15:48
 */
@Configuration
public class ZuulConfig {

    @Bean
    public AccessFilter accessFilter(){
        return new AccessFilter();
    }
}
