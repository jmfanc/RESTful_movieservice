package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "DIRECTORS")
public class Director extends Person {

    public Director(String name) {
        super(name);
    }
}
