package com.tomaszstankowski.movieservice.repository;


import com.tomaszstankowski.movieservice.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID>, JpaSpecificationExecutor<Person> {
}
