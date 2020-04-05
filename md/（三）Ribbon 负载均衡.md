### Ribbon 负载均衡

[TOC]

### 一、概述

#### 1、是什么

Spring Cloud Ribbon 是基于Netflix Ribbon 实现的一套 **`客户端 负载均衡的工具`**。

简单的说，Ribbon 是 Netflix 发的开源项目，主要功能是提供**`客户端的软件负载均衡算法`**，将Netflix 的中间层服务连接在一起。Ribbon 客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出 Load Balancer （简称LB）后面所有的机器，Ribbon 会自动的帮助你基于某种规则（例如简单轮询、随机连接等）去连接这些机器。我们也很容易使用Ribbon 实现自定义的负载均衡算法。

#### 2、（LB）负载均衡

LB，即负载均衡（Load Balance），在微服务或分布式集群中经常用的一种应用。

> 负载均衡简单的说就是将用户的请求平摊的分配到多个服务，从而达到HA。
>
> 常见的负载均衡有软件 Nginx，LVS，硬件 F5等。
>
> 相应的中间件：dubbo和SpringCloud中均给我们提供了负载均衡，`Spring Cloud 的负载均衡算法可以自定义。`

- Ribbon本地负载均衡客服端 VS Ngnix服务端负载均衡区别

  Ngnix 是服务器负载均衡，客户端所有请求都会交给ngnix，然后由ngnix实现转发请求。即负载均衡是由服务器端实现。
  
  Ribbon 本地负载均衡，在调用微服务接口时，会在注册中心上获取注册信息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术。

- 集中式LB

  即在服务的消费方和提供方之间独立使用独立的LB设置（可以是硬件，如 F5，也可以是软件，如 Nginx），由该设施负责把访问请求通过某种策略转发至服务的提供方。

- 进程内LB

  将LB逻辑集成到消费方，消费方从服务注册中心获取有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。

  **`Ribbon 就属于进程内 LB`**，他只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。 

#### 3、[官网  https://github.com/Netflix/ribbon/wiki/Getting-Started ](https://github.com/Netflix/ribbon/wiki/Getting-Started )



### 二、Ribbon 配置初步

1、修改 spring-cloud-example-consumer-person-80 工程

2、修改 `pomx.xml`

```xml
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
```



3、修改 `application.yml`,追加eureka的服务注册地址

```yml
server:
  port: 80
  
eureka:
  client:
    register-with-eureka: false
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
```



4、对ConfigBean 进行新注解 `@LoadBalanced`，获得rest 时加入Ribbon 的配置

```java
package com.mqf.study.cfgbeans;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean {
    @Bean
    @LoadBalanced//Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端       负载均衡的工具。
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
```



5、主启动类 `PersonConsumer80_APP`添加 **`@EnableEurekaClient`**

```java
package com.mqf.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PersonConsumer80_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer80_APP.class,args);
    }
}
```



6、修改 `PersonController_Customer`客户端访问类

```java
package com.mqf.study.controller;

import java.util.List;

import com.mqf.study.beans.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PersonController_Customer {

//	private static final String REST_URL_PREFIX = "http://localhost:6001";
	private static final String REST_URL_PREFIX = "http://SPRING-CLOUD-EXAMPLE-PERSON";

	/**
	 * 使用 使用restTemplate访问restful接口非常的简单粗暴无脑。 (url, requestMap,
	 * ResponseBean.class)这三个参数分别代表 REST请求地址、请求参数、HTTP响应转换被转换成的对象类型。
	 */
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/consumer/person/add")
	public boolean add(Person person)
	{
		return restTemplate.postForObject(REST_URL_PREFIX + "/person/add", person, Boolean.class);
	}

	@RequestMapping(value = "/consumer/person/get/{id}")
	public Person get(@PathVariable("id") Long id)
	{
		return restTemplate.getForObject(REST_URL_PREFIX + "/person/get/" + id, Person.class);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consumer/person/list")
	public List<Person> list()
	{
		return restTemplate.getForObject(REST_URL_PREFIX + "/person/list", List.class);
	}

	// 测试@EnableDiscoveryClient,消费端可以调用服务发现
	@RequestMapping(value = "/consumer/person/discovery")
	public Object discovery()
	{
		return restTemplate.getForObject(REST_URL_PREFIX + "/person/discovery", Object.class);
	}

}
```



7、先启动 2个 eureka 集群后，再启动 spring-cloud-example-provider-person-6001并注册进 eureka

8、启动 spring-cloud-example-consumer-person-80

9、测试

- <http://localhost/consumer/person/get/1> 
- <http://localhost/consumer/person/list> 

10、小结

> Ribbon 和 Rureka 整合后Consumer可以直接调用服务而不用在关心地址和端口号 
>
> ```java
> private static final String REST_URL_PREFIX = "http://localhost:6001";
> 变为
> private static final String REST_URL_PREFIX = "http://SPRING-CLOUD-EXAMPLE-PERSON";
> ```





### 三、Ribbon 负载均衡

#### 1、架构说明

- Ribbon 在工作时分成两步
- 第一步先选择 Eureka Server，它优先选择在同一个区域内负载较少的 server
- 第二步再根据用户指定的策略，在从 server 取到的服务注册列表中选择一个地址。

#### 2、参考 spring-cloud-example-provider-person-6001 ，创建 xxx-6002，xxx-6003

- spring-cloud-example-provider-person-6002
- spring-cloud-example-provider-person-6003

#### 3、新建 6002，6003数据库，各自微服务分别连接各自的数据库

#### 4、修改6002,6003各自的yml

- 端口
- 数据库连接
- 对外暴露的统一的服务实例名

#### 5、启动Eureka 集群配置区

#### 6、启动Person 微服务并各自测试通过

#### 7、启动 spring-cloud-example-consumer-person-80

#### 8、客户端通过 Ribbon 完成负载均衡并访问上一步的 Person 微服务

#### 9、总结

**`Ribbon 其实就是一个软负载均衡的客户端组件，它可以和其它所需请求的客户端结合使用，和eureka 结合只是其中一个实例。`**

### 四、[Ribbon 核心组件IRule](https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers )

>  IRule ： 根据特定的算法中从服务列表中选取一个要访问的服务

- `RoundRobinRule`  ：轮询
- `RandomRule` ：随机
- `AvailabilityFilteringRule` ：会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，还有并发的连接数量超过阀值的服务，然后对剩余的服务列表按照轮询策略进行访问
- `WeightedResponseTimeRule` ：根据平均响应时间计算所有服务器的权重，响应时间越快服务器权重越大，被选中的概率越高。刚启动动时如果统计信息不足，则使用 `RoundRobinRule`  策略，等统计信息足够，会切换到 `WeightedResponseTimeRule` 
- `RetryRule` ： 先按照 `RoundRobinRule`  的策略获取服务，如果获取服务失败则在指定的时间内进行重试，获取可用的服务
- `BestAvailableRule` ：会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务
- `ZoneAvoidanceRule`：默认规则，复合判断 server 所在区域的性能和server的可用性选择服务器

### 五、Ribbon 自定义

#### 1、修改 spring-cloud-example-consumer-person-80

#### `2、主启动类添加 @RibbonClient`

> 在启动该微服务时，就能去加载我们自定义Ribbon配置类，从而使配置生效
>
> @RibbonClient(name = "SPRING-CLOUD-EXAMPLE-PERSON",configuration = MySelfRule.class)

```java
import com.mqf.config.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "SPRING-CLOUD-EXAMPLE-PERSON",configuration = MySelfRule.class)
public class PersonConsumer80_APP {
    public static void main(String[] args) {
        SpringApplication.run(PersonConsumer80_APP.class,args);
    }
}
```



#### 3、配置细节 

> 官方文档明确给出了警告：
>
> 这个自定义配置类不能放在 `@ComponentScan` 所扫描的当前包及子包下，否则我们自定义的这个配置类就会被所有的Ribbon 客户端所共享，也就是说，我们达不到特殊化定制的目的了。
>
> <http://cloud.spring.io/spring-cloud-netflix/1.4.x/single/spring-cloud-netflix.html#_customizing_the_ribbon_client> 

#### 4、自定义



```java
package com.mqf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;

@Configuration
public class MySelfRule {
	@Bean
	public IRule myRule() {
		//return new RandomRule();// Ribbon默认是轮询，我自定义为随机
		//return new RoundRobinRule();// Ribbon默认是轮询，我自定义为随机
		
		return new RandomRule_MQF();// 我自定义为每台机器3次
	}
}
```

```java
package com.mqf.config;

import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public class RandomRule_MQF extends AbstractLoadBalancerRule {

    private int total = 0;            // 总共被调用的次数，目前要求每台被调用5次
    private int currentIndex = 0;    // 当前提供服务的机器号

    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes only get more
                 * restrictive.
                 */
                return null;
            }

            if (total < 3) {
                server = upList.get(currentIndex);
                total++;
            } else {
                total = 0;
                currentIndex++;
                if (currentIndex >= upList.size()) {
                    currentIndex = 0;
                }
            }


            if (server == null) {
                /*
                 * The only time this should happen is if the server list were somehow trimmed.
                 * This is a transient condition. Retry after yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;

    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        // TODO Auto-generated method stub

    }

}
```



