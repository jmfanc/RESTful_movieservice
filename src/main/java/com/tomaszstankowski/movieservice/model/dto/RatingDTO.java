package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
public class RatingDTO implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private short rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ShowDTO show;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDTO user;

    public RatingDTO(long id,
                     short rating,
                     Date date,
                     ShowDTO show,
                     UserDTO user) {
        this.id = id;
        this.rating = rating;
        this.date = date;
        this.show = show;
        this.user = user;
    }
}
