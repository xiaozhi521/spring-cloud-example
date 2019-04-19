## Rest微服构建

- spring-cloud-example-api：封装整体entity、接口、公共配置
- spring-cloud-example-provider-person-6001 ：微服务落地的服务提供者，6001端口
- spring-cloud-example-consumer-person-80：微服务调用的客户端使用，80端口

### 一、SpringCloud版本

```xml
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
```



### 二、构建步骤

#### 1、spring-cloud-example 整体父工程Project

- 新建 spring-cloud-example ，`注意：packaging 是 pom 模式 ` 

- 主要是定义 pom 文件，将后续各个子模块公用的jar包等统一提出来，类似一个抽象父类

- pom.xml

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

  

#### 2、spring-cloud-example-api 公共子模块Module

- 新建 spring-cloud-example-api，创建完成后检查父类`pom.xml`

- 修改 `pom.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <parent><!-- 子类里面显示声明才能有明确的继承表现，无意外就是父类的默认版本否则自己定义 -->
          <artifactId>spring-cloud-example</artifactId>
          <groupId>com.mqf.study</groupId>
          <version>1.0-SNAPSHOT</version>
      </parent>
      <modelVersion>4.0.0</modelVersion>
  
      <artifactId>spring-cloud-example-api</artifactId><!-- 当前Module我自己叫什么名字 -->
  
      <dependencies><!-- 当前Module需要用到的jar包，按自己需求添加，如果父类已经包含了，可以不用写版本号 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
          </dependency>
      </dependencies>
  
  </project>
  ```

  

- 新建人员Entity且配合lombok使用

  ```java
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  import lombok.experimental.Accessors;
  
  import java.io.Serializable;
  
  @SuppressWarnings("serial")
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Accessors(chain = true)
  public class Person2 implements Serializable {
      private static final long serialVersionUID = 1L;
  
      /**
       * 主键ID
       */
      private Long id;
  
      /**
       * 姓名
       */
      private String name;
  
      /**
       * 年龄
       */
      private Integer age;
  
      /**
       * 邮箱
       */
      private String email;
  }
  ```

  

- mvn clean install 后给其它模块引用，达到通用的目的。

  即需要用到部门实体的话，不用每个工程都定义一份，直接引用本模块即可

#### 3、spring-cloud-example-provider-person-6001 用户微服务提供者

- 新建spring-cloud-example-provider-person-6001

- pom.xml

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

  

- yml

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
        
  ```

  

- 工程`src/main/resources`目录下新建 ` mybatis` 文件夹后再建 `mybatis.cfg.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  	<settings>
  		<setting name="cacheEnabled" value="true" /><!-- 二级缓存开启 -->
  	</settings>
  
  </configuration>
  ```

  

- MySQL 创建人员数据库脚本

  ```sql
  CREATE DATABASE mp;
  
  DROP TABLE IF EXISTS person;
  
  CREATE TABLE person
  (
  	id BIGINT(20) NOT NULL COMMENT '主键ID',
  	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
  	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
  	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
  	PRIMARY KEY (id)
  );
  
  DELETE FROM person;
  
  INSERT INTO person (id, name, age, email) VALUES
  (1, 'Jone', 18, 'test1@baomidou.com'),
  (2, 'Jack', 20, 'test2@baomidou.com'),
  (3, 'Tom', 28, 'test3@baomidou.com'),
  (4, 'Sandy', 21, 'test4@baomidou.com'),
  (5, 'Billie', 24, 'test5@baomidou.com');
  ```

  

- `PersonDao` 接口

  ```java
  package com.mqf.study.dao;
  
  import com.mqf.study.beans.Person;
  import org.apache.ibatis.annotations.Mapper;
  
  import java.util.List;
  
  @Mapper
  public interface PersonDao {
      public boolean addPerson(Person dept);
  
      public Person findById(Long id);
  
      public List<Person> findAll();
  }
  ```

  

- 工程`src/main/resources/mybatis`目录下新建 `mapper` 文件夹后再建 `PersonMapper.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.mqf.study.dao.PersonDao">
  	<select id="findById" resultType="Person" parameterType="Long">
  		select id,name,age,email from person where id=#{id};
  	</select>
  	<select id="findAll" resultType="Person">
  		select id,name,age,email from person;
  	</select>
  	<insert id="addPerson" parameterType="Person">
  		INSERT INTO person(name,age,email) VALUES(#{name},#{age},#{email});
  	</insert>
  </mapper>
  ```

  

- `PersonService` 人员服务接口

  ```java
  package com.mqf.study.service;
  
  import com.mqf.study.beans.Person;
  
  import java.util.List;
  
  public interface PersonService {
  	public boolean add(Person person);
  
  	public Person get(Long id);
  
  	public List<Person> list();
  }
  ```

  

- `PersonServiceImpl` 人员服务接口实现类

  ```java
  package com.mqf.study.service.impl;
  
  import java.util.List;
  
  import com.mqf.study.beans.Person;
  import com.mqf.study.dao.PersonDao;
  import com.mqf.study.service.PersonService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  @Service
  public class DeptServiceImpl implements PersonService {
  	@Autowired
  	private PersonDao dao;
  
  
  	@Override
  	public boolean add(Person person) {
  		return dao.addPerson(person);
  	}
  
  	@Override
  	public Person get(Long id) {
  		return dao.findById(id);
  	}
  
  	@Override
  	public List<Person> list() {
  		return dao.findAll();
  	}
  }
  ```

  

- `PersonController`人员微服务提供者REST

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
  
  	@RequestMapping(value = "/person/add", method = RequestMethod.POST)
  	public boolean add(@RequestBody Person person){
  		return service.add(person);
  	}
  
  	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
  	public Person get(@PathVariable("id") Long id){
  		return service.get(id);
  	}
  
  	@RequestMapping(value = "/person/list", method = RequestMethod.GET)
  	public List<Person> list(){
  		return service.list();
  	}
  
  }
  
  ```

  

- `PersonProvider6001_APP`主启动类

  ```java
  package com.mqf.study;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  
  @SpringBootApplication
  public class PersonProvider6001_APP {
      public static void main(String[] args) {
          SpringApplication.run(PersonProvider6001_APP.class,args);
      }
  }
  ```

  

- 测试 ： <http://localhost:6001/person/get/1> 

  `{"id":1,"name":"Jone","age":18,"email":"test1@baomidou.com"}`

  

#### 4、spring-cloud-example-consumer-person-80 用户微服务消费者Module

- 新建spring-cloud-example-consumer-person-80

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
  
      <artifactId>spring-cloud-example-consumer-person-80</artifactId>
      <description>人员微服务消费者</description>
  
      <dependencies>
          <dependency><!-- 自己定义的api -->
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
      </dependencies>
  </project>
  ```

  

- `yml`

  ```yaml
  server:
    port: 80
  ```

  

- `com.mqf.study.cfgbeans`包下`ConfigBean`的编写（类似spring里面的applicationContext.xml 写入的注入Bean）

  ```java
  package com.mqf.study.cfgbeans;
  
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.client.RestTemplate;
  
  @Configuration
  public class ConfigBean {
      @Bean
      public RestTemplate getRestTemplate() {
          return new RestTemplate();
      }
  }
  ```

  

- `com.mqf.study.controller`包下新建`PersonController_Customer`人员微服务消费者REST

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
  
  	private static final String REST_URL_PREFIX = "http://localhost:6001";
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
  
  }
  ```

  

- `PersonConsumer80_APP`启动类

  ```java
  package com.mqf.study;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  @SpringBootApplication
  public class PersonConsumer80_APP {
      public static void main(String[] args) {
          SpringApplication.run(PersonConsumer80_APP.class,args);
      }
  }
  ```

  

- 测试

  - <http://localhost/consumer/person/get/1> 

    ```
    {"id":1,"name":"Jone","age":18,"email":"test1@baomidou.com"}
    ```

  - <http://localhost/consumer/person/add?name=mqf&age=16&email=mqf@163.com> 

    ```
    true
    ```

  - <http://localhost/consumer/person/list> 

    ```
    [{"id":1,"name":"Jone","age":18,"email":"test1@baomidou.com"},{"id":2,"name":"Jack","age":20,"email":"test2@baomidou.com"},{"id":3,"name":"Tom","age":28,"email":"test3@baomidou.com"},{"id":4,"name":"Sandy","age":21,"email":"test4@baomidou.com"},{"id":5,"name":"Billie","age":24,"email":"test5@baomidou.com"},{"id":6,"name":"mqf","age":16,"email":"mqf@163.com"}]
    ```

#### 

