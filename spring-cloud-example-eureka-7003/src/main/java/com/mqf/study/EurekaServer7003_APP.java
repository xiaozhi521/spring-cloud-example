package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName EurekaServer7001_APP
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/16 17:35
 */
@SpringBootApplication
@EnableEurekaServer // EurekaServer服务器端启动类,接受其它微服务注册进来
public class EurekaServer7003_APP {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServer7003_APP.class,args);
    }
}
