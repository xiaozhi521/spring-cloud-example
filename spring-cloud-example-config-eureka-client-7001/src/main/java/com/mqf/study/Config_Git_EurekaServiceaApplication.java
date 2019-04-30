package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EurekaServer 服务器端启动类,接受其它微服务注册进来
 * @ClassName Config_Git_EurekaServiceaApplication
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/30 16:05
 */

@SpringBootApplication
@EnableEurekaServer
public class Config_Git_EurekaServiceaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Config_Git_EurekaServiceaApplication.class,args);
    }
}
