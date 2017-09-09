package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "SERIES")
public class Serial extends Show implements Serializable {

    private short seasons;

    public Serial() {
    }

    public Serial(String title, String description, Date releaseDate, String location,
                  short seasons) {
        super(title, description, releaseDate, location);
        this.seasons = seasons;
    }
}
