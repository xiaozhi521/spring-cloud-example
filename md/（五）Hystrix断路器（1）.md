### Hystrix断路器

[TOC]

#### 一、概述

##### 1、分布式系统面临的问题

复杂分布式体系结构中的应用程序有数十个依赖关系，梅哥依赖关系在某些时候将不可避免的失败。

> **服务雪崩**
>
> 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“`扇出`”。
>
> 如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“`雪崩效应`”。

对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其它系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系失败，不能取消整个应用程序或系统。

##### 2、是什么

Hystrix 是一个用于处理分布式系统的`延迟`和`容错`的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时，异常等，**`Hystrix 能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性.`**

断路器 本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），`向调用方法返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方法无法处理的异常`，这样就保证了服务调用方的线程不会被长时间、不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。



##### 3、能干什么

服务降级、服务熔断、服务限流、接近实时监控

##### 4、[官网](https://github.com/Netflix/Hystrix/wiki/How-To-Use )

<https://github.com/Netflix/Hystrix/wiki/How-To-Use> 

#### 二、服务熔断

##### 1、是什么

熔断机制是应对雪崩效应的一种微服务链路保护机制。

当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务降级，`进而熔断该节点微服务的调用，快速返回“错误”的响应信息。`当检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阀值，缺省是5秒内20次调用失败就会启动熔断机制。熔断机制的注解是 **`@HystrixCommand`**。

##### 2、创建 spring-cloud-example-provider-person-hystrix-6001

-   参考 spring-cloud-example-provider-person-6001 创建 spring-cloud-example-provider-person-hystrix-6001

-   pom.xml

    -   修改内容

        ```xml
        <!-- hystrix -->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-hystrix</artifactId>
                </dependency>
        ```

        

    -   完整内容

        ```xml
        <dependencies>
                <!-- hystrix -->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-hystrix</artifactId>
                </dependency>
                <!-- 引入自己定义的api通用包，可以使用Dept部门Entity -->
                <dependency>
                    <groupId>com.mqf.study</groupId>
                    <artifactId>spring-cloud-example-api</artifactId>
                    <version>${project.version}</version>
                </dependency>
                <!-- actuator监控信息完善 -->
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-actuator</artifactId>
                </dependency>
                <!-- 将微服务provider侧注册进eureka -->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-eureka</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-config</artifactId>
                </dependency>
                <dependency>
                    <groupId>junit</groupId>
                    <artifactId>junit</artifactId>
                </dependency>
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                </dependency>
                <dependency>
                    <groupId>com.alibaba</groupId>
                    <artifactId>druid</artifactId>
                </dependency>
                <dependency>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-core</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
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
                <!-- 修改后立即生效，热部署 -->
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

        

-   yml

-   修改`PersonController`

    -   **`@HystrixCommand报异常后如何处理`**

        一旦调用服务方法失败并抛出了错误信息后，会自动调用 @HystrixCommand 标注好的 fallbackMethod 调用类中的指定方法

    -   代码

        ```java
        package com.mqf.study.controller;
        
        import com.mqf.study.beans.Person;
        import com.mqf.study.service.PersonService;
        import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RestController;
        
        @RestController
        public class PersonController {
        	@Autowired
        	private PersonService service;
        
        	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
        	//一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
        	@HystrixCommand(fallbackMethod = "processHystrix_Get")
        	public Person get(@PathVariable("id") Long id) {
        		Person person = service.get(id);
        		if (null == person) {
        			throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
        		}
        		return person;
        	}
        
        	public Person processHystrix_Get(@PathVariable("id") Long id){
        		return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
        				.setDbSource("no this database in MySQL");
        	}
        
        }
        
        ```

        

-   修改主启动类`PersonProvider6001_Hystrix_App`，并添加新注解 **`@EnableCircuitBreaker`**

    ```java
    package com.mqf.study;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
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
    @EnableCircuitBreaker//对hystrixR熔断机制的支持
    public class PersonProvider6001_Hystrix_App {
        public static void main(String[] args) {
            SpringApplication.run(PersonProvider6001_Hystrix_App.class,args);
        }
    }
    ```

    

-   测试

    -   eureka先启动
    -   PersonProvider6001_Hystrix_App 启动
    -   spring-cloud-example-consumer-person-80 启动
    -   测试 ： http://localhost/consumer/person/get/99

#### 三、服务降级

##### 1、是什么

服务降级，就是对不怎么重要的服务进行低优先级的处理。说白了，就是尽可能的把系统资源让给优先级高的服务。资源有限，而请求是无限的。如果在并发高峰期，不做服务降级处理，一方面肯定会影响整体服务的性能，严重的话可能会导致宕机某些重要的服务不可用。所以，一般在高峰期，为了保证网站核心功能服务的可用性，都要对某些服务降级处理。



##### 2、服务降级处理是在客户端实现完成的，与服务端没有关系

##### 3、修改 `spring-cloud-example-api` 工程

-   根据已有的 `PersonClientService` 接口新建一个实现了 `FallBackFactory`  接口的类 `PersonClientServiceFallBackFactory`

```java
package com.mqf.study.service;

import com.mqf.study.beans.Person;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonClientServiceFallBackFactory implements FallbackFactory<PersonClientService> {
    @Override
    public PersonClientService create(Throwable throwable) {
        return new PersonClientService() {
            @Override
            public Person get(Long id) {
                return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,Consumer客户端提供的降级信息,此刻服务Provider已经关闭")
                        .setDbSource("no this database in MySQL");
            }

            @Override
            public List<Person> list() {
                return null;
            }

            @Override
            public boolean add(Person dept) {
                return false;
            }
        };
    }
}
```



-   `PersonClientService`  接口 `再注解@FeignClient 添加fallbackFactory 属性值`

```java
package com.mqf.study.service;

import com.mqf.study.beans.Person;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *  一个实现了FallbackFactory接口的类 PersonClientServiceFallBackFactory
 */
//@FeignClient("SPRING-CLOUD-EXAMPLE-PERSON")
@FeignClient(value = "SPRING-CLOUD-EXAMPLE-PERSON",fallbackFactory=PersonClientServiceFallBackFactory.class)
public interface PersonClientService {
    @RequestMapping(value = "/person/add", method = RequestMethod.POST)
    public boolean add(Person person);

    @RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
    public Person get(@PathVariable("id") Long id);

    @RequestMapping(value = "/person/list", method = RequestMethod.GET)
    public List<Person> list();
}
```



##### 5、修改 `spring-cloud-example-consumer-person-feign`  的 `yml`

-   添加部分

    ```yaml
    feign:
      hystrix:
        enabled: true
    ```

    

-   全部

    ```yaml
    server:
      port: 80
      
    feign:
      hystrix:
        enabled: true
    
    eureka:
      client:
        register-with-eureka: false
        service-url:
          defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
    
    ```

    

##### 6、测试

-   启动 eureka 集群
-   启动 spring-cloud-example-provider-person-6001
-   启动 spring-cloud-example-consumer-person-feign
-   正常访问测试 ： <http://localhost/consumer/person/get/1> 
-   关闭微服务 spring-cloud-example-provider-person-6001
-   客户端调用提示

 

#### 四、服务监控 hystrix Dashboard

##### 1、概述

除了隔离依赖服务的调用外，Hystrix还提供了`准实时的调用监控（Hystrix Dashboard）`，Hystrix 会持续地记录所有通过Hystrix发起请求的执行信息，并以统计报表和图形的形式展示给用户，包括每秒执行多少请求，多少成功，多少失败等。 Netflix 通过`hystrix-metrics-event-stream` 项目实现了对以上指标的监控。Spring Cloud 也提供了 Hystrix Dashboard 的整合，对监控内容转化成可视化界面。

##### 2、步骤

###### 2.1、新建工程 spring-cloud-example-consumer-hystrix-dashboard

###### 2.2、pom.xml

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

    <artifactId>spring-cloud-example-consumer-hystrix-dashboard</artifactId>

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
        <!-- hystrix和 hystrix-dashboard相关 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
        </dependency>
    </dependencies>

</project>
```



###### 2.3、yaml

```yaml
server:
  port: 9001
```



###### 2.4、新建主启动类 `PersonConsumer_Dashboard_App` 并且添加 `@EnableHystrixDashboard`注解

```java
package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
public class PersonConsumer_Dashboard_App {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer_Dashboard_App.class,args);
    }
}
```



###### 2.5、`所有 Provider 微服务提供类（6001、6002、6003）都需要监控依赖配置`

```xml
<!-- actuator监控信息完善 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```



###### 2.6、启动  spring-cloud-example-consumer-hystrix-dashboard 改微服务监控消费端

-   <http://localhost:9001/hystrix.stream> 

###### 2.7、启动 eureka 集群

###### 2.8、启动 spring-cloud-example-provider-person-hystrix-6001

-    http://localhost:6001/person/get/1
-   http://localhost:6001/hystrix.stream

###### 2.9、启动的相关服务工程

###### 2.10、监控测试

-   delay : 该参数用来控制服务器上轮询监控信息的延迟时间，默认 2000 毫秒，可以通过配置该属性来降低客户端的网络和CPU消耗

-   Title：该参数对应了头部标题 Hystrix Stream 之后的内容，默认会使用具体监控实例的URL，可以通过配置该信息来展示更合适的标题。

-   如何看

    -   7色

    -   1圈

        >   实心圈：共有两种含义。他通过颜色的变化代表了实例的健康程度。他的健康从绿色<黄色<橙色<红色递减。
        >
        >   该实心圆除了颜色变化外，他的大小也会根据实例的请求流量发生变化，流量越大该实心圆越大。所以通过该实心圆的展示，就可以在大量的实例中快速的发现**故障实例**和**高压力实例**。

    -   1线

        >   曲线：用来记录2分钟内流量的相对变化，可以通过他来观察到流量的上升和下降的趋势。

    -   整图说明

        ![https://img-blog.csdnimg.cn/20190423120448390.jpg ]( )