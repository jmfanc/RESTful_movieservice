package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "RATINGS")
public class Rating implements Serializable {
    @Id
    private final UUID id = UUID.randomUUID();
    private final short rating;
    private final Date date;
    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private final Show show;
    @ManyToOne
    @JoinColumn(name = "LOGIN", nullable = false)
    private final User user;
}
