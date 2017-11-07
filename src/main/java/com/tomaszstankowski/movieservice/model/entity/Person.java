package com.tomaszstankowski.movieservice.model.entity;

import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "people")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<Participation> participations = new ArrayList<>();

    @ElementCollection(targetClass = Profession.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "people_professions", joinColumns = @JoinColumn(name = "person_id"))
    private Set<Profession> professions = new HashSet<>();

    @Column(name = "date_added")
    private Date dateAdded;

    @PrePersist
    private void prePersist() {
        dateAdded = new Date();
        dateModified = new Date();
    }

    @Column(name = "date_modified")
    private Date dateModified;

    @PreUpdate
    private void preUpdate() {
        dateModified = new Date();
    }

    public Person() {
    }

    public Person(String name, Date birthDate, String birthPlace, Sex sex) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
    }
}
