package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.entity.Person;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

public class PersonSpecifications {

    public static Specification<Person> nameContains(String phrase) {
        return (root, query, builder) -> builder.like(root.<String>get("name"), "%" + phrase + "%");
    }

    public static Specification<Person> youngerThan(int birthYear) {
        Date birthDate = new GregorianCalendar(birthYear, 11, 31).getTime();
        return (root, query, builder) -> builder.greaterThan(root.<Date>get("birthDate"), birthDate);
    }

    public static Specification<Person> olderThan(int birthYear) {
        Date birthDate = new GregorianCalendar(birthYear, 0, 31).getTime();
        return (root, query, builder) -> builder.lessThan(root.<Date>get("birthDate"), birthDate);
    }

    public static Specification<Person> isMale() {
        return (root, query, builder) -> builder.equal(root.<Sex>get("sex"), Sex.MALE);
    }

    public static Specification<Person> isFemale() {
        return (root, query, builder) -> builder.equal(root.<Sex>get("sex"), Sex.FEMALE);
    }

    public static Specification<Person> isActor() {
        return (root, query, builder) -> builder.isMember(Profession.ACTOR, root.<Set<Profession>>get("professions"));
    }

    public static Specification<Person> isDirector() {
        return (root, query, builder) -> builder.isMember(Profession.DIRECTOR, root.<Set<Profession>>get("professions"));
    }

    public static Specification<Person> isScreenwriter() {
        return (root, query, builder) -> builder.isMember(Profession.SCREENWRITER, root.<Set<Profession>>get("professions"));
    }
}
