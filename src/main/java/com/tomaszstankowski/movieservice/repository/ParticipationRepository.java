package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Participation;
import com.tomaszstankowski.movieservice.model.entity.Person;
import com.tomaszstankowski.movieservice.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByPersonAndRole(Person person, Person.Profession profession);

    List<Participation> findByShowAndRole(Show show, Person.Profession profession);
}
