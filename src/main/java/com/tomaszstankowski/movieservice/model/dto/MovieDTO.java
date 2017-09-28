package com.tomaszstankowski.movieservice.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovieDTO extends ShowDTO implements Serializable {

    private short duration;

    private int boxoffice;

    public MovieDTO() {
    }

    public MovieDTO(String title,
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
