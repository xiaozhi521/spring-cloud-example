package com.mqf.study.service;

import com.mqf.study.beans.Person;

import java.util.List;

public interface PersonCacheService {
	public boolean add(Person person);

	public Person get(Long id);

	public List<Person> list();
}
