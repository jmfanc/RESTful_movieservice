package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public abstract class ShowDTO {

    private long id;

    private String title;

    private String description;

    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String location;

    private Set<String> genres = new HashSet<>();

    public ShowDTO() {
    }

    public ShowDTO(String title,
                   String description,
                   Date releaseDate,
                   String location) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.location = location;
    }
}
