package com.mqf.study.service.impl;

import com.mqf.study.beans.Person;
import com.mqf.study.dao.PersonDao;
import com.mqf.study.service.PersonCacheService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *  遗留问题：hystrix 的 @CacheKey 无效
 */

@Service
public class PersonCacheServiceImpl implements PersonCacheService {
	@Autowired
	private PersonDao dao;

	Logger logger = LoggerFactory.getLogger(PersonCacheServiceImpl.class);


	@Override
	public boolean add(Person person) {
		return dao.addPerson(person);
	}

	@Override
	@HystrixCommand(fallbackMethod = "processHystrix_Get")
	public Person get(@CacheKey("id") Long id) {
		logger.info( " service : " + id);
		Person person = dao.findById(id);
		if(person == null){
			return processHystrix_Get(id);
		}
		return dao.findById(id);
	}

	@Override
	public List<Person> list() {
		return dao.findAll();
	}


	/**
	 *  不带异常内容的服务降级方法
	 * @param id
	 * @return
	 */
	public Person processHystrix_Get(Long id){
		return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
				.setDbSource("no this database in MySQL");
	}
}
