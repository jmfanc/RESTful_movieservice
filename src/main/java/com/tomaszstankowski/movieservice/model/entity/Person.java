package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PEOPLE")
public class Person {

    public enum Profession {ACTOR, DIRECTOR, SCREENWRITER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @Column(name = "BIRTH_PLACE")
    private String birthPlace;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<Participation> participations;

    @ElementCollection(targetClass = Profession.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PEOPLE_PROFFESIONS", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "PROFFESION")
    private Set<Profession> professions = new HashSet<>();

    public Person() {
    }

    public Person(String name, Date birthDate, String birthPlace, Sex sex) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
    }
}
