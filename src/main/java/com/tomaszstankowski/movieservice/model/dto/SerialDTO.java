package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class SerialDTO extends ShowDTO implements Serializable {

    private short seasons;

    @JsonCreator
    public SerialDTO(@JsonProperty("title") String title,
                     @JsonProperty("description") String description,
                     @JsonProperty("releaseDate") Date releaseDate,
                     @JsonProperty("location") String location,
                     @JsonProperty("genres") Set<String> genres,
                     @JsonProperty("seasons") short seasons) {
        super(title, description, releaseDate, location, genres);
        this.seasons = seasons;
    }

    public SerialDTO(long id,
                     String title,
                     String description,
                     Date releaseDate,
                     String location,
                     Set<String> genres,
                     float rating,
                     long rateCount,
                     short seasons) {
        super(id, title, description, releaseDate, location, genres, rating, rateCount);
        this.seasons = seasons;
    }
}
