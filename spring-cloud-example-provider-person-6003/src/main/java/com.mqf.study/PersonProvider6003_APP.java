package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName UserProvider8001_APP
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/15 17:23
 */
@SpringBootApplication
@EnableEurekaClient     //本服务启动后自动会自动注册进 eureka 服务中
@EnableDiscoveryClient  //服务发现
public class PersonProvider6003_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonProvider6003_APP.class,args);
    }
}
