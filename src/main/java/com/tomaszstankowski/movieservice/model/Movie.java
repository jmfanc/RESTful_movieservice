package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "MOVIES")
public class Movie extends Show implements Serializable {

    private short duration;

    private int boxoffice;

    public Movie() {
    }

    public Movie(String title, String description, Date releaseDate, String location, Director director,
                 short duration, int boxoffice) {
        super(title, description, releaseDate, location, director);
        this.duration = duration;
        this.boxoffice = boxoffice;
    }
}
