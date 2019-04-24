### Spring Cloud Config 分布式配置中心-服务端配置

[TOC]



#### 1、用自己的GitHub 账号 在 GitHub上新建一个名为 `spring-cloud-example-config` 的 Respository

#### 2、由第一步获得SSH协议的git地址 

>   git@github.com:xiaozhi521/spring-cloud-example-config.git

#### 3、本地硬盘目录上新建git仓库并clone

-   本地地址 ： E:\idea\mySpringCloud
-   git 命令 ： git clone git@github.com:xiaozhi521/spring-cloud-example-config.git

#### 4、在本地 `E:\idea\mySpringCloud\spring-cloud-example-config` 里新建一个 application.yml

```yaml
spring: 
    profiles: 
        active: 
        - dev
---
spring: 
    profiles: dev	#开发环境
    application: 
        name: spring-cloud-example-config-mqf-dev	
---

spring: 
    profiles: test	#测试环境
    application: 
        name: spring-cloud-example-config-mqf-test	
# 请保存UTF-8格
```



#### 5、将第4步的文件推送到github上

-   git add application.yml
-    git commit -m "first commit"
-   git push -u origin master

#### 6、新建Module模块`spring-cloud-example-config-3344` ， 它即为Cloud的配置中心模块

#### 7、POM

#### 8、YML

#### 9、主启动类 `Config_3344_StartSpringCloudApp`

#### 10、windows 下修改hosts文件，增加映射

```xml
127.0.0.1	config-3344.com
```



#### 11、测试通过 Config微服务是否可以从GitHub 上获取配置内容

-   启动 `spring-cloud-example-config-3344`
-   <http://config-3344.com:3344/application-dev.yml> 

#### 12、配置读取规则

#### 13、

