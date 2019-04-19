package com.mqf.study;

import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * @ClassName PersonConsumer80_APP
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/16 11:57
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients //开启 Feign
public class PersonConsumerFeign_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumerFeign_APP.class,args);
    }
}
