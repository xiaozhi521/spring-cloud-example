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
//	private static final String REST_URL_PREFIX = "http://MICROSERVICECLOUD-DEPT";

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
