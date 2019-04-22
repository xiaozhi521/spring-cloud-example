package com.mqf.study.controller;

import com.mqf.study.beans.Person;
import com.mqf.study.service.PersonService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {
	@Autowired
	private PersonService service;

	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
	//一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
	@HystrixCommand(fallbackMethod = "processHystrix_Get")
	public Person get(@PathVariable("id") Long id) {
		Person person = service.get(id);
		if (null == person) {
			throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
		}
		return person;
	}

	public Person processHystrix_Get(@PathVariable("id") Long id){
		return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
				.setDbSource("no this database in MySQL");
	}

}
