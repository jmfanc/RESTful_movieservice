package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "SHOWS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Show {
    @Id
    private final UUID id = UUID.randomUUID();
    private String title;
    private String description;
    private Date releaseDate;
    private String location;
    @ManyToMany
    @JoinTable(name = "SHOWS_GENRES")
    private List<Genre> genres;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Performance> performances;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Rating> ratings;

    public Show() {
    }

    public Show(String title, String description, Date releaseDate, String location) {
        //todo
    }
}
