package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PEOPLE")
public class Person implements Serializable {

    public enum Proffesion {ACTOR, DIRECTOR}

    @Id
    private final UUID id = UUID.randomUUID();

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
    @CollectionTable(name = "PEOPLE_PROFFESIONS")
    @Column(name = "PROFFESION")
    private Collection<Proffesion> proffesions;

    public Person() {
    }

    public Person(String name, Date birthDate, String birthPlace, Sex sex, Collection<Proffesion> proffesions) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
        this.proffesions = proffesions;
    }
}
