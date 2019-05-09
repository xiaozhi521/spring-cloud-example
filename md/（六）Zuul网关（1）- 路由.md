### [Zuul](https://github.com/Netflix/zuul/wiki ) 路由网关

[TOC]

#### 一、概述

Zuul 包含了对请求的**`路由`**和**`过滤`**两个最主要的功能：

其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础；而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础。Zuul 和 Eureka 进行整合，将Zuul 自身注册为Eureka 服务治理下的应用，同时从Eureka 中获得其他微服务的消息，也即以后的访问微服务都是通过 Zuul 跳转后获得。

>   **注意：Zuul 服务最终还是会注册进Eureka**

`提供 = 代理 + 路由 + 过滤三大功能`



#### 二、路由基本配置

##### 1、新建 module ： spring-cloud-example-zuul-gateway-9527

##### 2、pom.xml

-   部门内容

    ```xml
     <!-- zuul路由网关 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zuul</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    ```

    

-   全部内容

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <parent>
            <artifactId>spring-cloud-example</artifactId>
            <groupId>com.mqf.study</groupId>
            <version>1.0-SNAPSHOT</version>
        </parent>
        <modelVersion>4.0.0</modelVersion>
    
        <artifactId>spring-cloud-example-zuul-gateway-9527</artifactId>
    
        <dependencies>
            <!-- zuul路由网关 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-zuul</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-eureka</artifactId>
            </dependency>
            <!-- actuator监控 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
            <!-- hystrix容错 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-hystrix</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-config</artifactId>
            </dependency>
            <!-- 日常标配 -->
            <dependency>
                <groupId>com.mqf.study</groupId>
                <artifactId>spring-cloud-example-api</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jetty</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
            </dependency>
            <!-- 热部署插件 -->
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>springloaded</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
            </dependency>
        </dependencies>
    </project>
    ```

    

##### 3、yaml

```yaml
server: 
  port: 9527
 
spring: 
  application:
    name: spring-cloud-example-zuul-gateway
 
eureka: 
  client: 
    service-url: 
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka  
  instance:
    instance-id: gateway-9527.com
    prefer-ip-address: true 
 
info:
  app.name: spring-cloud-example
  company.name: www.mqf.com
  build.artifactId: $project.artifactId$
  build.version: $project.version$
```



##### 4、hosts 修改

127.0.0.1 myzuul.com

##### 5、主启动类 `Zuul_9527_StartSpringCloudApp`   ， **`@EnableZuulProxy`**

```java
package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class Zuul_9527_StartSpringCloudApp {
	public static void main(String[] args){
		SpringApplication.run(Zuul_9527_StartSpringCloudApp.class, args);
	}
}
```



##### 6、启动

-   eureka 集群
-   一个服务提供类 spring-cloud-example-provider-person-6001
-   一个路由

##### 7、测试

-   不用路由： <http://localhost:6001/person/get/1> 
-   启用路由： http://myzuul.com:9527/spring-cloud-example-person/person/get/2



#### 三、路由访问映射规则

##### 1、修改 spring-cloud-example-zuul-gateway-9527 

##### 2、代理名称

>   before 
>
>   http://myzuul.com:9527/spring-cloud-example-person/person/get/2
>
>   ```xml
>   zuul:
>     routes:
>       mydept.serviceId: spring-cloud-example-person
>       mydept.path: /myperson/**
>   ```
>
>   after
>
>   http://myzuul.com:9527/myperson/person/get/2

##### 3、原真实服务名忽略

单个具体，多个可以用  `"*"`

```yaml
zuul:
  #ignored-services: spring-cloud-example-person
  ignored-services: "*"
  routes:
    mydept.serviceId: spring-cloud-example-person
    mydept.path: /myperson/**
```



##### 4、设置统一公共前缀

```yaml
zuul:
  #ignored-services: spring-cloud-example-person
  prefix: /mqf
  ignored-services: "*"
  routes:
    mydept.serviceId: spring-cloud-example-person
    mydept.path: /myperson/**
```

测试 ： [http://myzuul.com:9527/mqf/myperson/person/get/2](http://myzuul.com:9527/mqf/myperson/person/get/2)

##### 5、最后 yaml

```yaml
zuul:
  #ignored-services: spring-cloud-example-person
  prefix: /mqf
  ignored-services: "*"
  routes:
    mydept.serviceId: spring-cloud-example-person
    mydept.path: /myperson/**
```



