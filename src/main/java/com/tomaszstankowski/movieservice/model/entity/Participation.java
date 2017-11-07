package com.tomaszstankowski.movieservice.model.entity;

import com.tomaszstankowski.movieservice.model.enums.Profession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "participations")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    private Profession role;

    private String info;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    public Participation() {
    }

    public Participation(Profession role, String info, Person person, Show show) {
        this.role = role;
        this.info = info;
        this.person = person;
        this.show = show;
    }

}
