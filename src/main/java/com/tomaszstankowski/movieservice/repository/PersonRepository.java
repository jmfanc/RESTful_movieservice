package com.tomaszstankowski.movieservice.repository;


import com.tomaszstankowski.movieservice.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Person findByNameAndBirthDateAndBirthPlace(String name, Date birthDate, String birthPlace);
}
