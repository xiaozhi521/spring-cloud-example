package com.mqf.study.config;

import com.mqf.study.filter.AccessFilter;
import com.mqf.study.filter.AccessFilter_2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ZuulConfig
 * AccessFilter 与 AccessFilter_2 二选一
 *  AccessFilter 无 error JSOn 返回
 *  AccessFilter_2 有 error JSOn 返回，但是，此处就返回一次，再次请求就会返回正常的数据
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

//    @Bean
//    public AccessFilter_2 accessFilter_2(){
//        return new AccessFilter_2();
//    }
}
