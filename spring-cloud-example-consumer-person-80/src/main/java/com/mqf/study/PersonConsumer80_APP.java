package com.mqf.study;

import com.mqf.config.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * @ClassName PersonConsumer80_APP
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/16 11:57
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "SPRING-CLOUD-EXAMPLE-PERSON",configuration = MySelfRule.class)
public class PersonConsumer80_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer80_APP.class,args);
    }
}
