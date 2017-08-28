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

    private short rating;

    private Date date = new Date();

    @ManyToOne
    @JoinColumn(name = "SHOW_ID", nullable = false)
    private Show show;

    @ManyToOne
    @JoinColumn(name = "USER_LOGIN", nullable = false)
    private User user;

    public Rating() {
    }

    public Rating(short rating, Show show, User user) {
        this.rating = rating;
        this.show = show;
        this.user = user;
    }
}
