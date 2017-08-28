package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "DIRECTORS")
public class Director extends Person {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "director")
    private List<Show> shows = new ArrayList<>();

    public Director() {
    }

    public Director(String name, Date birthDate, String birthPlace, Sex sex) {
        super(name, birthDate, birthPlace, sex);
    }
}
