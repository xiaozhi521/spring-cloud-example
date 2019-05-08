Hystrix断路器

[TOC]

#### 一、Turbine

##### 1、创建 spring-cloud-example-consumer-hystrix-turbine

###### 1）、参考 spring-cloud-example-consumer-hystrix-dashboard 创建 spring-cloud-example-consumer-hystrix-turbine

###### 2）、pom.xml

-   添加以下依赖

    ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-turbine</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-netflix-turbine</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
    </dependency>
    ```

    

-   完整内容

    ```xml
    <dependencies>
        <!-- 自己定义的api -->
        <dependency>
            <groupId>com.mqf.study</groupId>
            <artifactId>spring-cloud-example-api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- 修改后立即生效，热部署 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>springloaded</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>
        <!-- Ribbon相关 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- feign相关 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>
        <!-- hystrix和 hystrix-turbine相关 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-turbine</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-turbine</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
        </dependency>
    </dependencies>
    ```

    

###### 3）、**application.yml** 

```yaml
server:
  port: 9002

spring:
  application:
    name: hystrix-dashboard-turbine

eureka:
  client:
#    register-with-eureka: false
#    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/

turbine:
  appConfig: SPRING-CLOUD-EXAMPLE-PERSON6001,SPRING-CLOUD-EXAMPLE-PERSON6002 #配置Eureka中的serviceId列表，表明监控哪些服务
  aggregator:	
    clusterConfig: default	#指定聚合哪些集群，多个使用”,”分割，默认为default
    clusterNameExpression: new String("default")	#指定集群名称，可以是三种类型的值
```



-   `turbine.appConfig` ：配置Eureka中的serviceId列表，表明监控哪些服务
-   `turbine.aggregator.clusterConfig` ：指定聚合哪些集群，多个使用”,”分割，默认为default。可使用`http://.../turbine.stream?cluster={clusterConfig之一}`访问
-   `turbine.clusterNameExpression` ：指定集群名称，可以是三种类型的值
    -   默认表达式为appName；此时`turbine.aggregator.clusterConfig`需要配置想要监控的应用名称；
    -   当为default时，`turbine.aggregator.clusterConfig`可以不写，因为默认就是default；
    -   当为metadata[‘cluster’]时，假设想要监控的应用配置了`eureka.instance.metadata-map.cluster: ABC`，则需要配置，同时`turbine.aggregator.clusterConfig: ABC`

###### 4）、主启动类

```java
@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class PersonConsumer_Turbine_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer_Turbine_App.class,args);
    }
}
```

###### 5）、至此 turbine 搭建完成

###### 6）、测试

-   eureka先启动
-   PersonConsumer_Turbine_App启动
-   测试 ： http://localhost:9002/hystrix 

##### 2、参考 spring-cloud-example-provider-person-hystrix-6001 创建 spring-cloud-example-provider-person-hystrix-turbine-6001 及 spring-cloud-example-provider-person-hystrix-turbine-6002

###### 1）、修改application.yml 文件

###### 2）、修改pom.xml文件

###### 3）、修改主启动类

```java
@SpringBootApplication
@EnableEurekaClient     //本服务启动后自动会自动注册进 eureka 服务中
@EnableDiscoveryClient  //服务发现
@EnableCircuitBreaker//对hystrixR熔断机制的支持
public class PersonProvider6001_Hystrix_Turbine_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonProvider6001_Hystrix_Turbine_App.class,args);
    }
}
```



```java
@SpringBootApplication
@EnableEurekaClient     //本服务启动后自动会自动注册进 eureka 服务中
@EnableDiscoveryClient  //服务发现
@EnableCircuitBreaker//对hystrixR熔断机制的支持
public class PersonProvider6002_Hystrix_Turbine_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonProvider6002_Hystrix_Turbine_App.class,args);
    }
}
```



###### 4）、测试

-   启动eureka
-   启动 PersonProvider6001_Hystrix_Turbine_App 及   PersonProvider6002_Hystrix_Turbine_App
-   启动 PersonConsumer_Turbine_App
-   访问 http://localhost:9002/hystrix 
-   监控流输入 http://localhost:9002/turbine.stream  点击Monitor Stream 按钮
    -   Delay：该参数用来控制服务器上轮询监控信息的延迟时间，默认为2000毫秒，我们可以通过配置该属性来降低客户端的网络和CPU消耗。 
    -    Title ：该参数对应了上图头部标题Hystrix Stream之后的内容，默认会使用具体监控实例的URL，我们可以通过配置该信息来展示更合适的标题 

