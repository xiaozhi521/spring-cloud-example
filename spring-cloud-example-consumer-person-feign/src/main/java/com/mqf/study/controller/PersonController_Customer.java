package com.mqf.study.controller;

import java.util.List;

import com.mqf.study.beans.Person;
import com.mqf.study.service.PersonClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController_Customer {

	@Autowired
	private PersonClientService personClientService;

	@RequestMapping(value = "/consumer/person/add")
	public boolean add(Person person) {
		return personClientService.add(person);
	}

	@RequestMapping(value = "/consumer/person/get/{id}")
	public Person get(@PathVariable("id") Long id) {
		return personClientService.get(id);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consumer/person/list")
	public List<Person> list() {
		return personClientService.list();
	}

}
