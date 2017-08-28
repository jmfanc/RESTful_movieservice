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
    @ManyToOne
    @JoinColumn(name = "ACTOR_ID", nullable = false)
    private final Actor actor;
    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private final Show show;
    private String role;
}
