package com.tomaszstankowski.movieservice.model.dto;

import com.tomaszstankowski.movieservice.model.enums.Profession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(of = "id")
public class ParticipationDTO implements Serializable {

    private long id;
    private Profession role;
    private String info;
    private PersonDTO person;
    private ShowDTO show;

    public ParticipationDTO() {
    }

    public ParticipationDTO(Profession role,
                            String info,
                            PersonDTO person,
                            ShowDTO show) {
        this.role = role;
        this.info = info;
        this.person = person;
        this.show = show;
    }
}
