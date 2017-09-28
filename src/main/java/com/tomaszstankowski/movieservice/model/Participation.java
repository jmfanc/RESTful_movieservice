package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PARTICIPATIONS")
public class Participation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Person.Profession role;

    private String info;

    @ManyToOne
    @JoinColumn(name = "PERFORMER_ID", nullable = false)
    private Person participant;

    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private Show show;

    public Participation() {
    }

    public Participation(Person.Profession role, String info, Person participant, Show show) {
        this.role = role;
        this.info = info;
        this.participant = participant;
        this.show = show;
    }

}
