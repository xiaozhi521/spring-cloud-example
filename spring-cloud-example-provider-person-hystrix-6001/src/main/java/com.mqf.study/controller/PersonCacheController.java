package com.mqf.study.controller;

import com.mqf.study.beans.Person;
import com.mqf.study.service.PersonCacheService;
import com.mqf.study.service.PersonService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *  测试 service 层 添加 @HystrixCommand 及@CacheKey
 */
@RestController
public class PersonCacheController {
	@Autowired
	private PersonCacheService service;

	Logger logger = LoggerFactory.getLogger(PersonCacheController.class);

	// localhost:6001/cache/get/1
	@RequestMapping(value = "/cache/get/{id}", method = RequestMethod.GET)
	public Person get(@PathVariable("id") Long id) {
		Person person = service.get(id);
//		logger.info(person.getName());
//		if (null == person) {
//			throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
//		}
		return person;
	}
}
