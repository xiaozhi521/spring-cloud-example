### Spring Cloud Config 分布式配置中心

[TOC]


### 三、Spring Cloud Config 客户端配置与测试

##### 1、在`E:\idea\mySpringCloud\spring-cloud-example-config` 路径下新建文件 spring-cloud-example-config-client.yml

##### 2、 修改 spring-cloud-example-config-client.yml 并提交到github上

```yml
spring:
  profiles:
    actives:
    - dev

---
server:
  port: 8201
spring:
  profiles: dev
  application:
    name: spring-cloud-example-config-client
eureka:
  client:
    service-url:
      defaultZone: http://eureka-dev.com:7001/eureka/

---
server:
  port: 8202
spring:
  profiles: test
  application:
    name: spring-cloud-example-config-client
eureka:
  client:
    service-url:
      defaultZone: http://eureka-test.com:7001/eureka/
```



##### 3、新建 spring-cloud-example-config-client-3355

##### 4、修改pom文件

```xml
<dependencies>
        <!-- SpringCloud Config客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>springloaded</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>
    </dependencies>
```



##### 5、bootstrap.yml

```yaml
spring:
  cloud:
    config:
      name: spring-cloud-example-config-client #需要从github上读取的资源名称，注意没有yml后缀名
      profile: test   #本次访问的配置项
      label: master   
      uri: http://config-3344.com:3344  #本微服务启动后先去找3344号服务，通过SpringCloudConfig获取GitHub的服务地址
 
```



-   application.yml 是用户级的资源配置项

-   bootstrap.yml 是系统级的，优先级更加高

    >   Spring Cloud 会创建一个 **Bootstrap Context**，作为Spring 应用的 **Application Context**的**父上下文**。初始化的时候，**Bootstrap Context** 负责从外部资源配置属性并解析配置。这两个上下文共享一个从外部获取的 Environment。Bootstrap 属性有高优先级，默认情况下，他们不会被本地的配置覆盖。**Bootstrap Context** 和 **Application Context** 有着不同的约定，所以新增了一个 `bootstrap.yml`
    >
    >   文件，保证 **Bootstrap Context** 和 **Application Context** 配置的分离。

##### 6、application.yml

```yaml
spring:
  application:
    name: spring-cloud-example-config-client
```



##### 7、windows 下修改hosts文件，增加映射

127.0.0.1	config-client.com

##### 8、新建 rest 类，验证是否能从GitHub 上读取配置

```java
package com.mqf.study.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigClientRest {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${eureka.client.service-url.defaultZone}")
	private String eurekaServers;

	@Value("${server.port}")
	private String port;

	@RequestMapping("/config")
	public String getConfig() {
		String str = "applicationName: " + applicationName + "\t eurekaServers:" + eurekaServers + "\t port: " + port;
		System.out.println("******str: " + str);
		return "applicationName: " + applicationName + "\t eurekaServers:" + eurekaServers + "\t port: " + port;
	}
}
```



##### 9、主启动类 ConfigClient_3355_StartSpringCloudApp

```yml
package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigClient_3355_StartSpringCloudApp {
	public static void main(String[] args) {
		SpringApplication.run(ConfigClient_3355_StartSpringCloudApp.class, args);
	}
}
```



##### 10、测试

-   启动 Config 配置中心3344微服务并自测 ： http://config-3344.com:3344/application-dev.yml 
-   启动 3355作为client 准备访问
-   bootstrap.yml 里面的profile 值是什么，决定从 github上读取什么
    -   profile: dev
        -   dev 默认的端口号是8201 
        -   http://config-client.com:8201/config 
    -   profile: test
        -   test 默认的端口号是8202
        -   http://config-client.com:8202/config 

##### 11、成功实现客户端3355访问Spring Cloud Config 3344 通过 GitHub获取配置信息

