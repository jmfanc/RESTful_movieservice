package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "movies")
public class Movie extends Show {

    private short duration;

    private int boxoffice;

    public Movie() {
    }

    public Movie(String title,
                 String description,
                 Date releaseDate,
                 String location,
                 short duration,
                 int boxoffice) {
        super(title, description, releaseDate, location);
        this.duration = duration;
        this.boxoffice = boxoffice;
    }
}
