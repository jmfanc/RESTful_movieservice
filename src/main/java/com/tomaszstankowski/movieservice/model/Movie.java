package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "MOVIES")
public class Movie extends Show implements Serializable {
    private short duration;
    private int boxoffice;

    public Movie(String title) {
        super();
    }
}
