package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "SHOWS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    @Column(name = "RELEASE_DATE")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    private String location;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "SHOWS_GENRES",
            joinColumns = @JoinColumn(name = "SHOW_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_NAME", referencedColumnName = "NAME"))
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Rating> ratings = new ArrayList<>();

    public Show() {
    }

    public Show(String title,
                String description,
                Date releaseDate,
                String location) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.location = location;
    }
}
