package com.tomaszstankowski.movieservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "RATINGS")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private short rating;

    @Temporal(TemporalType.TIMESTAMP)
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
