package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PARTICIPATIONS")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    private Person.Profession role;

    private String info;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private Show show;

    public Participation() {
    }

    public Participation(Person.Profession role, String info, Person person, Show show) {
        this.role = role;
        this.info = info;
        this.person = person;
        this.show = show;
    }

}
