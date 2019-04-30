### Spring Cloud Config 分布式配置中心

[TOC]

### 四、Spring Cloud Config 配置实战

#### 目前情况

1、config 服务端配置OK且测试通过，我们可以和 Config + GitHub 进行配置修改并获得内容

2、此时我们做一个 eureka 服务 + 一个 Person 访问的微服务，将两个微服务的配置统一由github获得实现统一配置分布式管理，完成多环境的变更

#### 步骤

##### 1、本地文件的配置

###### 1.1、在本地 E:\idea\mySpringCloud\spring-cloud-example-config 路径下新建文件 spring-cloud-example-config-eureka-client.yml

###### 1.2、spring-cloud-example-config-eureka-client.yml 内容

```yaml
spring:
  profiles:
    actives:
    - dev

---
server:
  port: 7001    #注册中心占用7001端口，冒号后面必须要有空格

spring:
  profiles: dev
  application:
    name: spring-cloud-example-config-eureka-client

eureka:
  instance:
    hostname: eureka7001.com
  client:
    register-with-eureka: false   #当前的eureka-server 自己不注册进服务列表中
    fetch-registry: false   #不通过eureka获取注册信息
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/

---
server:
  port: 7001    #注册中心占用7001端口，冒号后面必须要有空格

spring:
  profiles: test
  application:
    name: spring-cloud-example-config-eureka-client

eureka:
  instance:
    hostname: eureka7001.com
  client:
    register-with-eureka: false   #当前的eureka-server 自己不注册进服务列表中
    fetch-registry: false   #不通过eureka获取注册信息
    service-url:
      defaultZone: http://eureka-test.com:7001/eureka/
```



###### 1.3、在本地 E:\idea\mySpringCloud\spring-cloud-example-config 路径下新建文件 spring-cloud-example-config-person-client.yml

###### 1.4、spring-cloud-example-config-person-client.yml 内容

```yaml
spring:
  profiles:
    actives:
    - dev

---
server:
  port: 6001

spring:
  profiles: dev
  application:
    name: spring-cloud-example-config-person-client
    datasource:
        type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
        url: jdbc:mysql://localhost:3306/cloudBD01?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC              # 数据库名称
        username: root
        password:                                               # 无密码
        dbcp2:
          min-idle: 5                                           # 数据库连接池的最小维持连接数
          initial-size: 5                                       # 初始化连接数
          max-total: 5                                          # 最大连接数
          max-wait-millis: 200                                  # 等待连接获取的最大超时时间
mybatis:
  config-location: classpath:mybatis/mybatis.cfg.xml        # mybatis配置文件所在路径
  type-aliases-package: com.mqf.study.beans    # 所有Entity别名类所在包
  mapper-locations:
  - classpath:mybatis/mapper/**/*.xml                       # mapper映射文件
eureka:
  client: #客户端注册进eureka服务列表内
    service-url:
#      defaultZone: http://localhost:7001/eureka
      defaultZone: http://eureka7001.com:7001/eureka/
  instance:
    instance-id: spring-cloud-example-person6001  #自定义服务名称信息
    prefer-ip-address: true     #访问路径可以显示IP地址
info:
  app.name: spring-cloud-example
  company.name: www.mqf.com
  build.artifactId: $project.artifactId$
  build.version: $project.version$

---
server:
  port: 6001

spring:
  profiles: test
  application:
    name: spring-cloud-example-config-person-client
    datasource:
        type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
        url: jdbc:mysql://localhost:3306/cloudBD02?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC              # 数据库名称
        username: root
        password:                                               # 无密码
        dbcp2:
          min-idle: 5                                           # 数据库连接池的最小维持连接数
          initial-size: 5                                       # 初始化连接数
          max-total: 5                                          # 最大连接数
          max-wait-millis: 200                                  # 等待连接获取的最大超时时间
mybatis:
  config-location: classpath:mybatis/mybatis.cfg.xml        # mybatis配置文件所在路径
  type-aliases-package: com.mqf.study.beans    # 所有Entity别名类所在包
  mapper-locations:
  - classpath:mybatis/mapper/**/*.xml                       # mapper映射文件
eureka:
  client: #客户端注册进eureka服务列表内
    service-url:
#      defaultZone: http://localhost:7001/eureka
      defaultZone: http://eureka7001.com:7001/eureka/
  instance:
    instance-id: spring-cloud-example-person6001  #自定义服务名称信息
    prefer-ip-address: true     #访问路径可以显示IP地址
info:
  app.name: spring-cloud-example
  company.name: www.mqf.com
  build.artifactId: $project.artifactId$
  build.version: $project.version$
```



##### 2、Config 版的 eureka 服务端

##### 2.1、新建  spring-cloud-example-config-eureka-client-7001

##### 2.2、pom

##### 2.3、bootstrap.yml

##### 2.4、application.yml

##### 2.5、主启动类 Config_Git_EurekaServiceaApplication

```java
/**
 * EurekaServer 服务器端启动类,接受其它微服务注册进来
 */

@SpringBootApplication
@EnableEurekaServer
public class Config_Git_EurekaServiceaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Config_Git_EurekaServiceaApplication.class,args);
    }
}
```



##### 2.6、测试

-   先启动 spring-cloud-example-config-3344
-   再启动 spring-cloud-example-config-eureka-client-7001
-   <http://eureka7001.com:7001/> 



##### 3、Config 办的person 微服务

##### 2.1、参考6001 拷贝后新建工程 spring-cloud-example-config-person-client-6001 

##### 2.2、pom

##### 2.3、bootstrap.yml

##### 2.4、application.yml

##### 2.5、主启动类及其它一套业务逻辑代码

##### 2.6、配置说明

##### 2.7、测试



