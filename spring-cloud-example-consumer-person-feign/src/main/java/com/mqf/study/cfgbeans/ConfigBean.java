package com.mqf.study.cfgbeans;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import feign.Logger;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName ConfigBean
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/16 9:35
 */
@Configuration
public class ConfigBean {
    @Bean
    @LoadBalanced//Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端       负载均衡的工具。
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IRule myRule() {
        return new RoundRobinRule();
//        return new RandomRule();//达到的目的，用我们重新选择的随机算法替代默认的轮询。
    }

    /**
     *  Feign 客户端默认的 Logger.Level 对象定义为 NONE 级别
     *  NONE: 不记录任何信息
     *  BASIC: 仅记录请求方法、URL以及响应状态码和执行时间
     *  HEADERS: 除了记录BASIC级别的信息之外， 还会记录请求和响应的头信息
     *  FULL:  记录所有请求与响应的明细， 包括头信息、 请求体、 元数据等
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}

//@Bean
//public UserServcie getUserServcie()
//{
//	return new UserServcieImpl();
//}
// applicationContext.xml == ConfigBean(@Configuration)
//<bean id="userServcie" class="com.atguigu.tmall.UserServiceImpl">
