package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "SERIES")
public class Serial extends Show {
    private short seasons;

    public Serial(String title) {
        super();
    }
}
