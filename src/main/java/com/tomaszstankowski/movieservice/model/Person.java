package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PEOPLE")
public class Person implements Serializable {

    public enum Proffesion {ACTOR, DIRECTOR}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "BIRTH_DATE")
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Column(name = "BIRTH_PLACE")
    private String birthPlace;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant")
    @JsonIgnore
    private List<Participation> participations;

    @ElementCollection(targetClass = Proffesion.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "PEOPLE_PROFFESIONS", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "PROFFESION")
    private Set<Proffesion> proffesions = new HashSet<>();

    public Person() {
    }

    public Person(String name, Date birthDate, String birthPlace, Sex sex, Set<Proffesion> proffesions) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
        this.proffesions = proffesions;
    }
}
