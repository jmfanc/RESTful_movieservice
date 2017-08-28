package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Column(name = "RELEASE_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String location;

    @ManyToMany
    @JoinTable(name = "SHOWS_GENRES",
            joinColumns = @JoinColumn(name = "SHOW_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_NAME", referencedColumnName = "NAME"))
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Performance> performances = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Rating> ratings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "DIRECTOR_ID", nullable = false)
    private Director director;

    public Show() {
    }

    public Show(String title, String description, Date releaseDate, String location, Director director) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.location = location;
        this.director = director;
    }
}
