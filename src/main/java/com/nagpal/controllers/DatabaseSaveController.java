package com.nagpal.controllers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.nagpal.entity.Person;
import com.nagpal.services.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@Log4j2
public class DatabaseSaveController {

    private final PersonService personService;

    public DatabaseSaveController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/save/basic")
    public ResponseEntity<String> basicSave() {

        List<Person> persons = loadCSV();

        long startTime = System.nanoTime();
        for (Person person : persons.subList(0, 100_000)) {
            personService.save(person);
        }
        //2166028941800 ns
        log.info("Total time for saving {} items using Basic save: {} ns ", 100000, System.nanoTime() - startTime);

        return ResponseEntity.ok("Saved");
    }

    @PostMapping("/save/batch")
    public ResponseEntity<String> batchSave() {
        List<Person> personList = loadCSV();

        long startTime = System.nanoTime();
        personService.saveBatch(personList);
        /*
            25k - 58774793200 ns
            30k - 65486475100 ns
            50k - 79242054900 ns
         */
        log.info("Total time for saving {} items using Batch save: {} ns ", personList.size(), System.nanoTime() - startTime);

        return ResponseEntity.ok("Batch saved.");
    }

    @PostMapping("/save/copy-save")
    public ResponseEntity<String> copySave() {
        List<Person> personList = loadCSV();

        long startTime = System.nanoTime();
        personService.saveAll(personList);
        //4266194600 ns
        log.info("Total time for saving {} items using Copy save: {} ns ", personList.size(), System.nanoTime() - startTime);

        return ResponseEntity.ok("Copy saved.");
    }

    private List<Person> loadCSV() {
        InputStream resourceAsStream = DatabaseSaveController.class.getResourceAsStream("person-list.csv");
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader(); // Use first row as header
        MappingIterator<Person> personIterator;
        List<Person> persons = new ArrayList<>();
        try {
            personIterator = csvMapper.readerFor(Person.class)
                    .with(csvSchema)
                    .readValues(resourceAsStream);

            persons = personIterator.readAll();
        } catch (IOException ioe) {
            throw new RuntimeException("Error reading file", ioe);
        }
        return persons;
    }

}
