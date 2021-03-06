package com.mqf.study.service;

import com.mqf.study.beans.Person;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *  一个实现了FallbackFactory接口的类 PersonClientServiceFallBackFactory
 */
//@FeignClient("SPRING-CLOUD-EXAMPLE-PERSON")
@FeignClient(value = "SPRING-CLOUD-EXAMPLE-PERSON",fallbackFactory=PersonClientServiceFallBackFactory.class)
public interface PersonClientService {
    @RequestMapping(value = "/person/add", method = RequestMethod.POST)
    public boolean add(Person person);

    @RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
    public Person get(@PathVariable("id") Long id);

    @RequestMapping(value = "/person/list", method = RequestMethod.GET)
    public List<Person> list();
}
