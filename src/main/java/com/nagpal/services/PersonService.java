package com.nagpal.services;

import com.nagpal.entity.Person;

import java.util.List;

public interface PersonService {

    void save(Person person);

    void saveBatch(List<Person> persons);

    void saveAll(List<Person> persons);
}
