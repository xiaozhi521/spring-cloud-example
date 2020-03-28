## Eureka 服务注册与发现

[TOC]

### 一、是什么 

NetFlix 在设计 Eureka 时遵守的就是 AP 原则。

Eureka 是 Netflix 的一个子模块，也是核心模块之一。Eureka 是一个基于REST的服务，用于定位服务，以实现云端中间层服务发现和故障转移。服务注册于发现对于微服务架构来说非常重要，有了服务发现与注册，`只需要使用服务的标识符，就可以访问到服务`，而不需要修改服务调用的配置文件了。`功能类似于dubbo的注册中心，比如Zookeeper`。

### 二、原理

#### 1、Eureka 基本架构

- 什么是服务注册与发现？

Spring Cloud 封装了 NetFlix 公司开发的 Eureka 模块来`实现服务注册和发现`。

Eureka 采用了C-S 的架构设计。Eureka Server 作为服务注册功能的服务器，它是服务注册中心。

而系统中的其他微服务，使用Eureka的客户端连接到 Eureka Server 并维持心跳连接。这样系统的维护人员就可以通过Eureka Server 来监控系统中各个微服务是否正常运行。

在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前自己服务器的信息，比如：服务地址、通讯地址等以别名方式注册到注册中心上。另一方（消费者|服务提供者），以该别名的方式去注册中心上获取到实际的服务通讯地址、然后再实现本地RPC调用RPC远程调用框架核心设计思想；在于注册中心，因为使用注册中心管理每个服务于服务之间的一个依赖关系（服务治理概念）。

**在任何一个RPC 远程框架中，都会有一个注册中心（存放服务地址相关的信息（接口地址））。**


Spring Cloud 的一些其它模块（比如Zuul）就可以通过 Eureka Server 来发现系统中的其它微服务，并执行相关的逻辑。



**Eureka 包含两个组件：`Eureka Server `  和 `Eureka Client`**

- Eureka Server 提供服务注册服务

  各个微服务节点通过配置启动后，会在Eureka Server 中进行注册，这样Eureka Server 中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可在界面中直观的看到

- Eureka Client

  是一个Java客户端，用于简化Eureka Server 的交互，客户端同时也具备一个内置的、使用轮询（round-robin）负载算法的负载均衡器。在应用启动后，将会向 Eureka Server 发送心跳（默认周期为30秒）。如果Eureka Server 在多个心跳周期内没有接收到某个节点的心跳，Eureka Server 将会从服务注册表中把这个服务节点移除（默认是90秒）。

#### 2、三大角色

- Eureka Server 提供服务注册和发现
- Service Provider 服务提供方将自身服务注册到Eureka，从而使服务消费方能够找到
- Service Consumer 服务消费方从Eureka 获取注册服务列表，从而能够消费服务

#### 3、我们工程情况

- 总父工程
- 通用模块api
- 服务提供者Provider
- 服务消费者Consumer

### 三、构建步骤

#### 1、spring-cloud-example-eureka-7001  Eureka服务注册中心Model

- 新建  spring-cloud-example-eureka-7001

- `pom.xml`

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
  
      <artifactId>spring-cloud-example-eureka-7001</artifactId>
  
      <dependencies>
          <!--eureka-server服务端 -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-eureka-server</artifactId>
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
  
  </project>
  ```

  

- `yml`

  ```yaml
  server: 
    port: 7001
   
  eureka: 
    instance:
      hostname: 127.0.0.1 #eureka服务端的实例名称
    client: 
      register-with-eureka: false     #false表示不向注册中心注册自己。
      fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
      service-url: 
         defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/       #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址（单机）。  
  ```

  

- `EurekaServer7001_APP` 主启动类

  ```java
  package com.mqf.study;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
  
  
  @SpringBootApplication
  @EnableEurekaServer // EurekaServer服务器端启动类,接受其它微服务注册进来
  public class EurekaServer7001_APP {
  
      public static void main(String[] args) {
          SpringApplication.run(EurekaServer7001_APP.class,args);
      }
  }
  ```

  

- 测试

  结果页面 ： `No instances available ` ，没有服务被发现，因为没有注册服务进来当然不可能有服务被发现

#### 2、spring-cloud-example-provider-person-6001   将已有的人员微服务注册进eureka服务中心

- 修改 spring-cloud-example-provider-person-6001 

- `pom.xml`

  - 修改 pom文件的部门内容

    ```xml
    <!-- 将微服务provider侧注册进eureka -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-eureka</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-config</artifactId>
            </dependency>
    ```

    

  - pom 文件完整内容

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
    
        <artifactId>spring-cloud-example-provider-person-6001</artifactId>
    
        <dependencies>
            <!-- 引入自己定义的api通用包，可以使用Dept部门Entity -->
            <dependency>
                <groupId>com.mqf.study</groupId>
                <artifactId>spring-cloud-example-api</artifactId>
                <version>${project.version}</version>
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
    
    </project>
    ```

    

- `yml`

  - 部分修改

    ```yml
    eureka:
      client: #客户端注册进eureka服务列表内
        service-url:
          defaultZone: http://localhost:7001/eureka
    ```

    

  - 完整yml

    ```yaml
    server:
      port: 6001
      
    mybatis:
      config-location: classpath:mybatis/mybatis.cfg.xml        # mybatis配置文件所在路径
      type-aliases-package: com.mqf.study.beans    # 所有Entity别名类所在包
      mapper-locations:
      - classpath:mybatis/mapper/**/*.xml                       # mapper映射文件
        
    spring:
       application:
        name: spring-cloud-example-user
       datasource:
        type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
        url: jdbc:mysql://localhost:3306/mp?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC              # 数据库名称
        username: root
        password:                                               # 无密码
        dbcp2:
          min-idle: 5                                           # 数据库连接池的最小维持连接数
          initial-size: 5                                       # 初始化连接数
          max-total: 5                                          # 最大连接数
          max-wait-millis: 200                                  # 等待连接获取的最大超时时间
          
    eureka:
      client: #客户端注册进eureka服务列表内
        service-url:
          defaultZone: http://localhost:7001/eureka
    ```

    

- `PersonProvider6001_APP` 主启动类

- 测试

  - ``先要启动Eureka Server``
  - [http://localhost:7001/](http://localhost:7001/)
  - 微服务注册名配置说明

#### 3、actutor 与注册微服务信息完善

- 主机名成：服务名称修改 

  - 当前问题

  - 修改 spring-cloud-example-provider-person-6001 yml文件

    - 修改部门内容

      ```yml
      instance:
          instance-id: spring-cloud-example-person6001 #自定义服务名称信息
      ```

      

    - 完整内容

      ```yml
      eureka:
        client: #客户端注册进eureka服务列表内
          service-url:
            defaultZone: http://localhost:7001/eureka
        instance:
          instance-id: spring-cloud-example-person6001 #自定义服务名称信息
      ```

      

- 访问信息有IP信息提示

  - 当前没有IP提示

  - 修改 spring-cloud-example-provider-person-6001 yml文件

    - 修改部门内容

      ```yml
      eureka:
        client: #客户端注册进eureka服务列表内
          service-url:
            defaultZone: http://localhost:7001/eureka
        instance:
          instance-id: spring-cloud-example-person6001	#自定义服务名称信息
          prefer-ip-address: true     #访问路径可以显示IP地址
      ```

      

- 微服务info内容详细信息

  - 当前问题 ：超链接点击服务报告 Error Page

  - 修改 spring-cloud-example-provider-person-6001 `pom`文件

    ```xml
    <!-- actuator监控信息完善 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```

    

  - 总的父工程   spring-cloud-example 修改 pom.xml 添加构建`build`信息

    - 添加 build 信息

      ```xml
      <build>
           <finalName>spring-cloud-example</finalName>
           <resources>
               <resource>
                   <directory>src/main/resources</directory>
                   <filtering>true</filtering>
               </resource>
           </resources>
           <plugins>
               <plugin>
                   <groupId>org.apache.maven.plugins</groupId>
                   <artifactId>maven-resources-plugin</artifactId>
                   <configuration>
                       <delimiters>
                           <delimit>$</delimit>
                       </delimiters>
                   </configuration>
               </plugin>
           </plugins>
      </build>
      ```

    - 完整的 pom.xml

      ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
          <modelVersion>4.0.0</modelVersion>
      
          <groupId>com.mqf.study</groupId>
          <artifactId>spring-cloud-example</artifactId>
          <packaging>pom</packaging>
          <version>1.0-SNAPSHOT</version>
      
          <modules>
              <module>spring-cloud-example-api</module>
              <module>spring-cloud-example-provider-person-6001</module>
              <module>spring-cloud-example-consumer-person-80</module>
              <module>spring-cloud-example-eureka-7001</module>
          </modules>
      
          <properties>
              <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
              <maven.compiler.source>1.8</maven.compiler.source>
              <maven.compiler.target>1.8</maven.compiler.target>
              <junit.version>4.12</junit.version>
              <log4j.version>1.2.17</log4j.version>
              <lombok.version>1.16.18</lombok.version>
          </properties>
      
          <dependencyManagement>
              <dependencies>
                  <dependency>
                      <groupId>org.springframework.cloud</groupId>
                      <artifactId>spring-cloud-dependencies</artifactId>
                      <version>Dalston.SR1</version>
                      <type>pom</type>
                      <scope>import</scope>
                  </dependency>
                  <dependency>
                      <groupId>org.springframework.boot</groupId>
                      <artifactId>spring-boot-dependencies</artifactId>
                      <version>1.5.9.RELEASE</version>
                      <type>pom</type>
                      <scope>import</scope>
                  </dependency>
                  <dependency>
                      <groupId>mysql</groupId>
                      <artifactId>mysql-connector-java</artifactId>
                      <version>5.0.4</version>
                  </dependency>
                  <dependency>
                      <groupId>com.alibaba</groupId>
                      <artifactId>druid</artifactId>
                      <version>1.0.31</version>
                  </dependency>
                  <dependency>
                      <groupId>org.mybatis.spring.boot</groupId>
                      <artifactId>mybatis-spring-boot-starter</artifactId>
                      <version>1.3.0</version>
                  </dependency>
                  <dependency>
                      <groupId>ch.qos.logback</groupId>
                      <artifactId>logback-core</artifactId>
                      <version>1.2.3</version>
                  </dependency>
                  <dependency>
                      <groupId>junit</groupId>
                      <artifactId>junit</artifactId>
                      <version>${junit.version}</version>
                      <scope>test</scope>
                  </dependency>
                  <dependency>
                      <groupId>log4j</groupId>
                      <artifactId>log4j</artifactId>
                      <version>${log4j.version}</version>
                  </dependency>
              </dependencies>
          </dependencyManagement>
      
          <build>
              <finalName>spring-cloud-example</finalName>
              <resources>
                  <resource>
                      <directory>src/main/resources</directory>
                      <filtering>true</filtering>
                  </resource>
              </resources>
              <plugins>
                  <plugin>
                      <groupId>org.apache.maven.plugins</groupId>
                      <artifactId>maven-resources-plugin</artifactId>
                      <configuration>
                          <delimiters>
                              <delimit>$</delimit>
                          </delimiters>
                      </configuration>
                  </plugin>
              </plugins>
          </build>
      
      
      </project>
      ```

      

    

  - 修改 spring-cloud-example-provider-person-6001 `yml` 文件

    - 修改部分

      ```yaml
      info:
        app.name: spring-cloud-example
        company.name: www.mqf.com
        build.artifactId: $project.artifactId$
        build.version: $project.version$
      ```

    - 完整yml

      ```yaml
      server:
        port: 6001
        
      mybatis:
        config-location: classpath:mybatis/mybatis.cfg.xml        # mybatis配置文件所在路径
        type-aliases-package: com.mqf.study.beans    # 所有Entity别名类所在包
        mapper-locations:
        - classpath:mybatis/mapper/**/*.xml                       # mapper映射文件
          
      spring:
         application:
          name: spring-cloud-example-person
         datasource:
          type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
          driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
          url: jdbc:mysql://localhost:3306/mp?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC              # 数据库名称
          username: root
          password:                                               # 无密码
          dbcp2:
            min-idle: 5                                           # 数据库连接池的最小维持连接数
            initial-size: 5                                       # 初始化连接数
            max-total: 5                                          # 最大连接数
            max-wait-millis: 200                                  # 等待连接获取的最大超时时间
            
      eureka:
        client: #客户端注册进eureka服务列表内
          service-url:
            defaultZone: http://localhost:7001/eureka
        instance:
          instance-id: spring-cloud-example-person6001  #自定义服务名称信息
          prefer-ip-address: true     #访问路径可以显示IP地址
      
      info:
        app.name: spring-cloud-example
        company.name: www.mqf.com
        build.artifactId: $project.artifactId$
        build.version: $project.version$
      
      ```

      

    

#### 4、`euraka 自我保护`

> **异常：**`**EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.**`
>
> 导致原因：1、某时刻某一个微服务不可用了，eureka 不会立刻清理，依旧会对该服务的信息进行保存
> 2、属于CAP里的AP分支



默认情况下，如果 Eureka Server 在一定时间内没有接收到某个微服务实例的心跳，Eureka Server 将会注销该实例（默认90秒）。但是当网络分区故障发生时，微服务与Eureka Server 之间无法正常通信，以上行为可能变得非常危险了，`因为微服务本身其实是健康的，此时本不应该注销这个服务`。Eureka 通过 “自我保护模式”来解决这个问题，当Eureka Server 节点在短时间内丢失过多的客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。一旦进入该模式，Eureka Server 就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。当网络故障恢复后，该Eureka Server 节点会自动退出自我保护模式。

`在自我保护模式中，Eureka Server会保护服务注册表中的信息，不再注销任何服务实例。当它收到的心跳数重新恢复到阀值以上时，该Eureka Server节点就会自动退出自我保护机制模式。他的设计哲学就是宁可暴露错误的服务注册信息，也不盲目注销任何肯能健康的服务实例。`



综上，自我保护模式是一种对应网络异常的安全保护措施。他的架构哲学是宁可同时保留所有的微服务（健康的和不健康的都会保留），也不盲目注销任何健康的微服务。使用自我保护模式。可以让 Eureka 集群更加的健壮、稳定。

> 解决方式：
>
> - 关闭自我保护模式（`eureka.server.enable-self-preservation`设为`false`）
>
>   



#### 5、spring-cloud-example-provider-person-6002   服务发现Discovery

- 对于注册进 Eureka 里面的微服务，可以通过服务发现来获得该服务的信息

- 修改  spring-cloud-example-provider-person-6002 工程的 PersonController

  ```java
  package com.mqf.study.controller;
  
  import java.util.List;
  
  import com.mqf.study.beans.Person;
  import com.mqf.study.service.PersonService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.cloud.client.ServiceInstance;
  import org.springframework.cloud.client.discovery.DiscoveryClient;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestMethod;
  import org.springframework.web.bind.annotation.RestController;
  
  @RestController
  public class PersonController {
  	@Autowired
  	private PersonService service;
  	@Autowired
  	private DiscoveryClient client;
  
  	@RequestMapping(value = "/person/add", method = RequestMethod.POST)
  	public boolean add(@RequestBody Person person) {
  		return service.add(person);
  	}
  
  	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
  	public Person get(@PathVariable("id") Long id) {
  		return service.get(id);
  	}
  
  	@RequestMapping(value = "/person/list", method = RequestMethod.GET)
  	public List<Person> list() {
  		return service.list();
  	}
  
  
  	@RequestMapping(value = "/person/discovery", method = RequestMethod.GET)
  	public Object discovery()
  	{
  		List<String> list = client.getServices();
  		System.out.println("**********" + list);
  
  		List<ServiceInstance> srvList = client.getInstances("SPRING-CLOUD-EXAMPLE-PERSON");
  		for (ServiceInstance element : srvList) {
  			System.out.println(element.getServiceId() + "\t" + element.getHost() + "\t" + element.getPort() + "\t"
  					+ element.getUri());
  		}
  		return this.client;
  	}
  
  }
  
  ```

  

- `PersonProvider6001_APP` 主启动类

  > @EnableDiscoveryClient  注解开启服务发现

  ```JAVA
  package com.mqf.study;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
  import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
  
  
  @SpringBootApplication
  @EnableEurekaClient     //本服务启动后自动会自动注册进 eureka 服务中
  @EnableDiscoveryClient  //服务发现
  public class PersonProvider6001_APP {
      public static void main(String[] args) {
          SpringApplication.run(PersonProvider6001_APP.class,args);
      }
  }
  ```

  

- 自测

  > 1.先启动Eureka Server
  >
  > 2.再启动 PersonProvider6001_APP ，需要稍等一会
  >
  > 3.<http://localhost:6001/person/discovery> 

- 修改 spring-cloud-example-consumer-person-80 工程的 `PersonController_Customer`

  

### 四、集群配置

- 原理

- 新建 spring-cloud-example-eureka-7002

- 按照 7001 为模板粘贴 POM

- 修改 7002 主启动类

- 修改映射配置

  - 修改 host 文件 ，位置 `C:\Windows\System32\drivers\etc`

  - ```host
    127.0.0.1 	eureka7001.com
    127.0.0.1 	eureka7002.com
    ```

    

- 2 台 eureka 服务器的yml配置

- spring-cloud-example-provider-person-6001 微服务发布到 7001,7002 eureka 集群配置中

  ```yaml
  server: 
    port: 7001
   
  eureka: 
    instance:
      hostname: eureka7001.com #eureka服务端的实例名称
    client: 
      register-with-eureka: false     #false表示不向注册中心注册自己。
      fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
      service-url: 
  #       defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/       #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址（单机）。
        defaultZone: http://eureka7002.com:7002/eureka/
  ```

  

### 五、作为服务注册中心，Eureka比Zookeeper好在哪里

- 传统的ACID

  > A（Atomicity）原子性
  >
  > C（Consistency）一致性
  >
  > I（Isolation）独立性
  >
  > D（Durability）持久性

- CAP

  > C （Consistency）强一致性
  >
  > A（Availability）可用性
  >
  > P（Partition tolerance）分区容错性

  `最多只能同时较好的满足两个。`

  CAP理论的的核心：一个分布式系统不可能同时很好的满足一致性、可用性和分区容错性这三个需求。因此，根据CAP原理将NoSQL 数据库分成了满足CA原则，满足CP原则和满足AP原则三大类：

  - CA ：单点集群，满足一致性，可用性的系统，通常性能不是特别高；RDBMS
  - CP ：满足一致性，分区容错性的系统，同城性能不是特别高；MongoDB、HBase、Reids
  - AP ：满足可用性，分区容错性的系统，通常可能对一致性要求低一些；大多数网站架构的选择

- CAP 的 3 进 2

  CAP理论就是说在分布式存储系统中，最多只能实现上面的两点。由于当前的网络硬件肯定会出现延迟丢包等问题，所以`分区容错性是我们必须需要实现的。`所以我们只能在 `一致性`和`可用性`之间进行权衡，没有NOSQL系统能同时保证这三点。

- `作为服务注册中心，Eureka比Zookeeper好在哪里`

  - zookeeper 保证CP

    当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但不能接受服务直接宕掉不可用。也就是说，服务注册功能对可用性的要求高于一致性。但是zk会出现这样一种情况，当master节点因为网络故障与其它节点失去联系时，剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30 ~ 120s，且选举期间整个zk集群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因网络问题使得zk集群失去master节点是较大概率会发生的值，虽然服务能够最终恢复，但是漫长的选举时间导致的注册长期不可用是不能容忍的。

  - Eureka 保证AP

    Eureka 看明白了这一点，因此在设计时就优先保证可用性。`Eureka各个节点都是平等的，`几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册或如果发现连接失败，则会自动切换至其它节点，只要有一台Eureka 还在，就能保证注册服务可用（保证可用性），只不过查到的信息可能不是最新的（不保证强一致性）。除此之外，Eureka 还有一种自我保护机制，如果15分钟内超过85%的节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：

    ​	1.Eureka 不再从注册列表中移除因为长时间没有收到心跳而应该过期的服务

    ​	2.Eureka 仍然能够接受新服务的注册和查询请求，但是不会被同步到其它节点上（既保证当前的节点依然可用）

    ​	3.当网络稳定时，当前实例新的注册信息会被同步到其它节点中

    `因此，Eureka可以很好的对应因网络故障导致部分节点失去联系的情况，而不会像Zookeeper那样使整个注册服务瘫痪。`





