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
public class MovieDTO extends ShowDTO implements Serializable {

    private short duration;

    private int boxoffice;

    @JsonCreator
    public MovieDTO(@JsonProperty("title") String title,
                    @JsonProperty("description") String description,
                    @JsonProperty("releaseDate") Date releaseDate,
                    @JsonProperty("location") String location,
                    @JsonProperty("genres") Set<String> genres,
                    @JsonProperty("duration") short duration,
                    @JsonProperty("boxoffice") int boxoffice) {
        super(title, description, releaseDate, location, genres);
        this.duration = duration;
        this.boxoffice = boxoffice;
    }

    public MovieDTO(long id,
                    String title,
                    String description,
                    Date releaseDate,
                    String location,
                    Set<String> genres,
                    float rating,
                    long rateCount,
                    short duration,
                    int boxoffice) {
        super(id, title, description, releaseDate, location, genres, rating, rateCount);
        this.duration = duration;
        this.boxoffice = boxoffice;
    }
}
