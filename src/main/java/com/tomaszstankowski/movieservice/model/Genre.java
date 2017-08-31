package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "GENRES")
public class Genre {
    @Id
    private String name;
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private List<Show> shows = new ArrayList<>();
}
