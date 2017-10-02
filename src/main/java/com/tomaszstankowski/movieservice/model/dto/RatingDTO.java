package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
public class RatingDTO implements Serializable {

    private long id;
    private short rating;
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private ShowDTO show;
    private UserDTO user;

    public RatingDTO() {
    }

    public RatingDTO(short rating, ShowDTO show, UserDTO user) {
        this.rating = rating;
        this.show = show;
        this.user = user;
    }
}
