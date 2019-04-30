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
	@Autowired
	private DiscoveryClient client;

	@RequestMapping(value = "/person/add", method = RequestMethod.POST)
	public boolean add(@RequestBody Person person) {
		return service.add(person);
	}

	@RequestMapping(value = "/person/get/{id}", method = RequestMethod.GET)
	public Person get(@PathVariable("id") Long id) {
		return service.get(id);
	}

	@RequestMapping(value = "/person/list", method = RequestMethod.GET)
	public List<Person> list() {
		return service.list();
	}


	@RequestMapping(value = "/person/discovery", method = RequestMethod.GET)
	public Object discovery()
	{
		List<String> list = client.getServices();
		System.out.println("**********" + list);

		List<ServiceInstance> srvList = client.getInstances("SPRING-CLOUD-EXAMPLE-PERSON");
		for (ServiceInstance element : srvList) {
			System.out.println(element.getServiceId() + "\t" + element.getHost() + "\t" + element.getPort() + "\t"
					+ element.getUri());
		}
		return this.client;
	}

}
