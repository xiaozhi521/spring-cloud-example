package com.mqf.study.service.impl;

import java.util.List;

import com.mqf.study.beans.Person;
import com.mqf.study.dao.PersonDao;
import com.mqf.study.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {
	@Autowired
	private PersonDao dao;


	@Override
	public boolean add(Person person) {
		return dao.addPerson(person);
	}

	@Override
	public Person get(Long id) {
		return dao.findById(id);
	}

	@Override
	public List<Person> list() {
		return dao.findAll();
	}
}
