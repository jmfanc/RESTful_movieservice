package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "GENRES")
@EqualsAndHashCode(of = "name")
public class Genre implements Serializable {

    @Id
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Show> shows = new HashSet<>();

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }
}
