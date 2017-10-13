package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@JsonDeserialize(using = ShowDTODeserializer.class)
public abstract class ShowDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private String title;

    private String description;

    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String location;

    private Set<String> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private float rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long rateCount;

    @JsonCreator
    public ShowDTO(@JsonProperty("title") String title,
                   @JsonProperty("description") String description,
                   @JsonProperty("releaseDate") Date releaseDate,
                   @JsonProperty("location") String location,
                   @JsonProperty("genres") Set<String> genres) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.location = location;
        this.genres = genres;
    }

    public ShowDTO(long id,
                   String title,
                   String description,
                   Date releaseDate,
                   String location,
                   Set<String> genres,
                   float rating,
                   long rateCount) {
        this(title, description, releaseDate, location, genres);
        this.id = id;
        this.rating = rating;
        this.rateCount = rateCount;
    }
}
