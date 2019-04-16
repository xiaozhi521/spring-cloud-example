package com.mqf.study.dao;

import com.mqf.study.beans.Person;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PersonDao {
    public boolean addPerson(Person person);

    public Person findById(Long id);

    public List<Person> findAll();
}
