### [Zuul](https://github.com/Netflix/zuul/wiki ) 路由网关 - 过滤器

[TOC]

​	为了实现对客户端请求的安全校验和权限控制， 最简单和粗暴的方法就是为每个微服务应用都实现一套用于校验签名和鉴别权限的过滤器或拦截器。 不过，这样的做法并不可取，它会增加日后系统的维护难度， 因为同一个系统中的各种校验逻辑很多情况下都是大致相同或类似的，这样的实现方式会使得相似的校验逻辑代码被分散到了各个微服务中去， 冗余代码的出现是我们不希望看到的。 所以， 比较好的做法是将这些校验逻辑
剥离出去， 构建出一个独立的鉴权服务。 

​	在完成了剥离之后，有不少开发者会直接在微服务应用中通过调用鉴权服务来实现校验，但是这样的做法仅仅只是解决了鉴权逻辑的分离，并没有在本质上将这部分不属于冗余的逻辑从原有的微服务应用中拆分出， 冗余的拦截器或过滤器依然会存在。

​	对千这样的问题， **更好的做法是通过前置的网关服务来完成这些非业务性质的校验**。由于网关服务的加入， 外部客户端访问我们的系统已经有了统 一 入口， 既然这些校验与具体业务无关， 那何不在请求到达的时候就完成校验和过滤， 而不是转发后再过滤而导致更长的请求延迟。 同时，通过在网关中完成校验和过滤， 微服务应用端就可以去除各种复杂的过滤器和拦截器了，这使得微服务应用接口的开发和测试复杂度也得到了相应降低。

[^摘自Spring Cloud 微服务实战]: 



#### 一、filter 包下新建 AccessFilter  类

```java
/**
 * @ClassName MyFilter
 * @Description 自定义实现 zuul 过滤工能
 * @Author mqf
 * @Date 2019/5/9 15:26
 */
@Component
public class AccessFilter extends  ZuulFilter {

    private static Logger log= LoggerFactory.getLogger(AccessFilter.class);
    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型：
     *  pre：可以在请求被路由之前调用
     *  route：在路由请求时候被调用
     *  post：在route和error过滤器之后被调用
     *  error：处理请求时发生错误时被调用
     * @return
     */
    @Override
    public String filterType() {
        return "pre"; //前置过滤器
    }

    @Override
    public int filterOrder() {
        return 0; //过滤器的执行顺序，数字越大优先级越低
    }

    @Override
    public boolean shouldFilter() {
        return true;//是否执行该过滤器，此处为true，说明需要过滤
    }

    /**
     * 过滤器具体逻辑
     * @return
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("send {} request to {}", request.getMethod(),request.getRequestURL().toString());

        Object accessToken = request.getParameter("accessToken");
        if(StringUtils.isEmpty(accessToken)) {
            log.warn("access token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }
        ctx.set("access token ok", true);// 设值，让下一个Filter看到上一个Filter的状态
        return null;
    }
}
```



#### 二、配置 AccessFilter 类，config 包下新建  ZuulConfig配置类

```java
@Configuration
public class ZuulConfig {

    @Bean
    public AccessFilter accessFilter(){
        return new AccessFilter();
    }
}
```

####三、主启动类

```java
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableEurekaClient
public class Zuul_9527_StartSpringCloudApp {
	public static void main(String[] args) {
		SpringApplication.run(Zuul_9527_StartSpringCloudApp.class, args);
	}
}
```

#### 四、测试

-   启动 eureka 集群
-   启动 spring-cloud-example-provider-person-6001
-   启动 spring-cloud-example-zuul-gateway-9527

-   不用路由： <http://localhost:6001/person/get/1> 
-   启用路由： http://myzuul.com:9527/spring-cloud-example-person/person/get/2



#### 五、小结 （用作前端与服务短的交互）

-   它作为系统的统一入口， 屏蔽了系统内部各个微服务的细节
-   它可以与服务治理框架结合，实现自动化的服务实例维护以及负载均衡的路由转发。
-   它可以实现接口权限校验与微服务业务逻辑的解耦。
-   通过服务网关中的过炖器， 在各生命周期中去校验请求的内容， 将原本在对外服务层做的校验前移， 保证了微服务的无状态性， 同时降低了微服务的测试难度， 让服务本身更集中关注业务逻辑的处理





