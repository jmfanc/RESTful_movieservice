package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Data()
@EqualsAndHashCode(callSuper = true)
@Entity(name = "ACTORS")
public class Actor extends Person implements Serializable {
    private short height;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "actor")
    private List<Performance> performances;

    public Actor(String name) {
        super(name);
    }
}
