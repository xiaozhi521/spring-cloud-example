### [Feign 负载均衡 ](https://cloud.spring.io/spring-cloud-static/spring-cloud-openfeign/2.1.0.RELEASE/single/spring-cloud-openfeign.html )

### Feign 文档

https://cloud.spring.io/spring-cloud-static/spring-cloud-openfeign/2.1.0.RELEASE/single/spring-cloud-openfeign.html 

### Spring Cloud 文档

https://projects.spring.io/spring-cloud/spring-cloud.html#spring-cloud-feign



[TOC]

### 简介

> Feign 是一个声明式WebService客户端。使用Feign能让编写Web Service 客户端更加简单，它的使用方法时定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。
>
> Feign 也支持可插拔式的编码器和解码器。Spring Cloud 对Feign进行封装，使其支持了Spring MVC 标准注解和 HttpMessageConverters。
>
> Feign可以与Eureka和Ribbon组合使用以支持负载均衡。 

**`只需创建一个接口，然后在上面添加注解即可`**

#### Feign 能干什么

前面开发使用 Ribbon + RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模板化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，`往往一个接口会被多处调用，所以通常会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。`所以，Feign 再此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，`我们只需要创建一个接口并使用注解的方式来配置它（以前是Dao接口上面标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解即可）`，即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon时，自动封装服务调用客户端的开发量。

#### Feign 集成了Ribbon

利用Ribbon 维护了SPRING-CLOUD-EXAMPLE-PERSON 的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，`通过feign只需要定义服务绑定接口且以声明式的方法`，优雅而简单的实现了服务调用

### Feign 使用

#### 1、参考 spring-cloud-example-consumer-person-80 新建 spring-cloud-example-consumer-person-feign

以下spring-cloud-example-consumer-person-feign  简化为 perosn-feign

#### 2、person-feign 工程修改 `pom.xml`文件，主要添加对 feign的支持

```xml
 <!-- feign相关 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```



#### 3、修改 spring-cloud-example-api 工程

`pom.xml`

```xml
 <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

新建 `PersonClientService` 接口

```java
package com.mqf.study.service;

import com.mqf.study.beans.Person;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("SPRING-CLOUD-EXAMPLE-PERSON")
public interface PersonClientService {
    @RequestMapping(value = "/person/add", method = RequestMethod.POST)
    public boolean add(Person person);

    @RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
    public Person get(@PathVariable("id") Long id);

    @RequestMapping(value = "/person/list", method = RequestMethod.GET)
    public List<Person> list();
}

```



#### 4、person-feign 工程修改`Controller`,添加上一步新建的 `PersonClientService`接口

```java
package com.mqf.study.controller;

import java.util.List;

import com.mqf.study.beans.Person;
import com.mqf.study.service.PersonClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController_Customer {

	@Autowired
	private PersonClientService personClientService;

	@RequestMapping(value = "/consumer/person/add")
	public boolean add(Person person) {
		return personClientService.add(person);
	}

	@RequestMapping(value = "/consumer/person/get/{id}")
	public Person get(@PathVariable("id") Long id) {
		return personClientService.get(id);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consumer/person/list")
	public List<Person> list() {
		return personClientService.list();
	}
	
}

```



#### 5、person-feign 工程修改主启动类

```java
package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients //开启 Feign
public class PersonConsumerFeign_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumerFeign_APP.class,args);
    }
}

```



#### 6、测试

- 启动eureka集群
- 启动微服务6001,6002,6003
- 启动feign
- <http://localhost/consumer/person/get/1> 
- Feign 自带负载均衡配置项

#### 7、总结

Feign 通过接口方法调用Rest服务，之前是Ribbon+RestTemplate

该请求发送给Eureka服务器（http://SPRING-CLOUD-EXAMPLE-PERSON/person/list）

通过Feign 直接找到服务接口，由于在进行服务调用的时候融合了Ribbon技术，所以也支持负载均衡。



