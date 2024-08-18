package com.nagpal.services.impl;

import com.nagpal.entity.Person;
import com.nagpal.repos.PersonRepo;
import com.nagpal.repos.PostgresCopyManager;
import com.nagpal.services.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonRepo personRepo;
    private PostgresCopyManager copyManager;

    public PersonServiceImpl(PersonRepo personRepo, PostgresCopyManager copyManager) {
        this.personRepo = personRepo;
        this.copyManager = copyManager;
    }

    @Override
    public void save(Person person) {
        personRepo.save(person);
    }

    @Override
    public void saveBatch(List<Person> persons) {
        personRepo.saveAll(persons);
    }

    @Override
    public void saveAll(List<Person> persons) {
        this.copyManager.prepareAndSaveToDB(persons);
    }
}
