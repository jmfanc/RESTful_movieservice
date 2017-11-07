package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "shows")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    @Column(name = "date_released")
    @Temporal(TemporalType.DATE)
    private Date dateReleased;

    private String location;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "shows_genres",
            joinColumns = @JoinColumn(name = "show_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_name", referencedColumnName = "name"))
    private Set<Genre> genres = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getShows().add(this);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
        genre.getShows().remove(this);
    }

    public void clearGenres() {
        Iterator<Genre> it = genres.iterator();
        while (it.hasNext()) {
            Genre g = it.next();
            g.getShows().remove(this);
            it.remove();
        }
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "show")
    private List<Rating> ratings = new ArrayList<>();

    @Column(name = "date_added")
    private Date dateAdded;

    @PrePersist
    private void prePersist() {
        dateAdded = new Date();
        dateModified = new Date();
    }

    @Column(name = "date_modified")
    private Date dateModified;

    @PreUpdate
    private void preUpdate() {
        dateModified = new Date();
    }

    public Show() {
    }

    public Show(String title,
                String description,
                Date dateReleased,
                String location) {
        this.title = title;
        this.description = description;
        this.dateReleased = dateReleased;
        this.location = location;
    }
}
