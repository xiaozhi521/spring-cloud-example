package com.mqf.study.controller;

import com.mqf.study.beans.Person;
import com.mqf.study.service.PersonService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *  https://www.cnblogs.com/hellxz/p/9056806.html
 */
@RestController
public class PersonController {
	@Autowired
	private PersonService service;

	Logger logger = LoggerFactory.getLogger(PersonController.class);

	/**
	 *  使用注解请求缓存 方式1
	 *  @CacheResult  标记这是一个缓存方法，结果会被缓存;该注解用来标记请求命令返回的结果应该被缓存，它必须与@HystrixCommand注解结合使用
	 *  @HystrixCommand
	 *		fallbackMethod : 服务降级；
	 *						@HystrixCommand注解修饰的函数的回调函数，
	 *						@HystrixCommand修饰的函数必须和这个回调函数定义在同一个类中，因为定义在了同一个类中，
	 *						所以fackback method可以是public/private均可。
	 *						如果要获取触发服务降级的具体异常内容，只需要在 fallback 实现方法的参数中增加 Throwable e 对象的定义
	 *		ignoreExceptions：忽略指定异常类型功能
	 *
	 *		通过设置命令组， Hysti·ix 会根据组来组织和统计命令的告警、 仪表盘等信息。
	 *		那么为什么一定要设置命令组呢？
	 * 			因为除了根据组能实现统计之外， Hystrix 命令默认的线程划分也是根据命令分组来实现的。
	 * 	 		默认情况下， Hystrix 会让相同组名的命令使用同一个线程池，
	 * 	 	  	所以我们需要在创建 Hystrix 命令时为其指定命令组名来实现默认的线程池划分。
	 * 	   	commandKey ： 配置全局唯一标识服务的名称，
	 * 	   				比如，库存系统有一个获取库存服务，那么就可以为这个服务起一个名字来唯一识别该服务;
	 * 	   					如果不配置，则默认是@HystrixCommand注解修饰的函数的函数名。
	 *
	 * 	 	groupKey ： 一个比较重要的注解，配置全局唯一标识服务分组的名称，比如，库存系统就是一个服务分组。
	 * 	 			通过设置分组，Hystrix会根据组来组织和统计命令的告、仪表盘等信息。Hystrix命令默认的线程划分也是根据命令组来实现。
	 * 	 			默认情况下，Hystrix会让相同组名的命令使用同一个线程池，所以我们需要在创建Hystrix命令时为其指定命令组来实现默认的线程池划分。
	 * 	 			此外，Hystrix还提供了通过设置threadPoolKey来对线程池进行设置。建议最好设置该参数，使用threadPoolKey来控制线程池组。
	 *
	 * 	 	threadPoolKey ： 对线程池进行设定，细粒度的配置，相当于对单个服务的线程池信息进行设置，也可多个服务设置同一个threadPoolKey构成线程组
	 * 	 	threadPoolProperties : 线程池相关参数设置，具体可以设置哪些参数见 com.netflix.hystrix.HystrixThreadPoolProperties
	 *		commandProperties : 配置该命令的一些参数，如executionIsolationStrategy配置执行隔离策略，默认是使用线程隔离，也可以配置为THREAD，即线程池隔离。
	 *				具体可以设置哪些参数见 com.netflix.hystrix.HystrixCommandProperties
	 *  @param id
	 *  @return
	 */
	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
	//一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
	@HystrixCommand(fallbackMethod = "processHystrix_Get",ignoreExceptions = {NumberFormatException.class},
			commandKey = "get",groupKey = "personGroup",threadPoolKey = "getPersonThreadPool",
			commandProperties = {
					@HystrixProperty(name = "execution.isolation.strategy", value="SEMAPHORE")
			},threadPoolProperties = {
				@HystrixProperty(name = "coreSize", value = "30"),
				@HystrixProperty(name = "maxQueueSize", value = "101"),
				@HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
				@HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
				@HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
				@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
		})
//	@CacheResult(cacheKeyMethod = "getCacheKey")
	public Person get(@PathVariable("id") Long id) {
		Person person = service.get(id);
		logger.info(person.getName());
		if (null == person) {
			throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
		}
		return person;
	}
	/**
	 *  不带异常内容的服务降级方法
	 * @param id
	 * @return
	 */
	public Person processHystrix_Get(@PathVariable("id") Long id){
		return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
				.setDbSource("no this database in MySQL");
	}

	/**
	 *  带有异常内容的服务降级方法
	 * @param id
	 * @param throwable
	 * @return
	 */
	public Person processHystrix_Get(@PathVariable("id") Long id,Throwable throwable){
		return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
				.setDbSource("no this database in MySQL");
	}
	/**
	 * 使用注解清除缓存 方式1
	 * @CacheRemove 必须指定commandKey才能进行清除指定缓存
	 * 	该注解用来让请求命令的缓存失效，失效的缓存根据commandKey进行查找。
	 */
	@CacheRemove(commandKey = "commandKey1", cacheKeyMethod = "getCacheKey")
	@HystrixCommand
	public void flushCacheByAnnotation(Long id){
		System.out.println("请求缓存已清空！");
		//这个@CacheRemove注解直接用在更新方法上效果更好
	}

	/**
	 * 第一种方法没有使用@CacheKey注解，而是使用这个方法进行生成cacheKey的替换办法
	 * 这里有两点要特别注意：
	 * 1、这个方法的入参的类型必须与缓存方法的入参类型相同，如果不同被调用会报这个方法找不到的异常
	 * 2、这个方法的返回值一定是String类型
	 */
	public String getCacheKey(Long id){
		return String.valueOf(id);
	}



}
