package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PERFORMANCES")
public class Performance implements Serializable {

    @Id
    private final UUID id = UUID.randomUUID();

    private String role;

    @ManyToOne
    @JoinColumn(name = "ACTOR_ID", nullable = false)
    private Actor actor;

    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private Show show;

    public Performance() {
    }

    public Performance(String role, Actor actor, Show show) {
        this.role = role;
        this.actor = actor;
        this.show = show;
    }

}
