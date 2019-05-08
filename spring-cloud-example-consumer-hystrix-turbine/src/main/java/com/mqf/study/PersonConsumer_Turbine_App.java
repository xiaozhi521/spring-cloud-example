package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @ClassName PersonConsumer_Dashboard_App
 * @Description TODO
 * @Author mqf
 * @Date 2019/5/8 11:22
 */
@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class PersonConsumer_Turbine_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer_Turbine_App.class,args);
    }
}
