package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data()
@EqualsAndHashCode(callSuper = true)
@Entity(name = "ACTORS")
public class Actor extends Person implements Serializable {

    private short height;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "actor")
    private List<Performance> performances = new ArrayList<>();

    public Actor() {
    }

    public Actor(String name, Date birthDate, String birthPlace, Sex sex, short height) {
        super(name, birthDate, birthPlace, sex);
        this.height = height;
    }
}
