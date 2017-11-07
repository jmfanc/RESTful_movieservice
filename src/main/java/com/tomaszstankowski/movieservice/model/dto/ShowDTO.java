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
    private Date dateReleased;

    private String location;

    private Set<String> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private float rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long rateCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateModified;

    @JsonCreator
    public ShowDTO(@JsonProperty("title") String title,
                   @JsonProperty("description") String description,
                   @JsonProperty("dateReleased") Date dateReleased,
                   @JsonProperty("location") String location,
                   @JsonProperty("genres") Set<String> genres) {
        this.title = title;
        this.description = description;
        this.dateReleased = dateReleased;
        this.location = location;
        this.genres = genres;
    }

    public ShowDTO(long id,
                   String title,
                   String description,
                   Date dateReleased,
                   String location,
                   Set<String> genres,
                   float rating,
                   long rateCount,
                   Date dateAdded,
                   Date dateModified) {
        this(title, description, dateReleased, location, genres);
        this.id = id;
        this.rating = rating;
        this.rateCount = rateCount;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }
}
