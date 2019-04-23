package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @ClassName PersonConsumer_Dashboard_App
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/23 11:22
 */
@SpringBootApplication
@EnableHystrixDashboard
public class PersonConsumer_Dashboard_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer_Dashboard_App.class,args);
    }
}
