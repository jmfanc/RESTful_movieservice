package com.tomaszstankowski.movieservice.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SerialDTO extends ShowDTO implements Serializable {

    private short seasons;

    public SerialDTO() {
    }

    public SerialDTO(String title,
                     String description,
                     Date releaseDate,
                     String location,
                     short seasons) {
        super(title, description, releaseDate, location);
        this.seasons = seasons;
    }
}
