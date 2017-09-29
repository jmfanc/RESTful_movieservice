package com.tomaszstankowski.movieservice.model.entity;

import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PEOPLE")
public class Person {

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
    private List<Participation> participations = new ArrayList<>();

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
