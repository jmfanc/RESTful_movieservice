package com.tomaszstankowski.movieservice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity(name = "GENRES")
public class Genre {
    @Id
    private final String genre;
    @ManyToMany(mappedBy = "genres")
    private List<Show> shows;
}
