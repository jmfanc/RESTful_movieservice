package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(of = "id")
public class ParticipationDTO implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private Profession role;

    private String info;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PersonDTO person;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ShowDTO show;

    @JsonCreator
    public ParticipationDTO(@JsonProperty("role") Profession role,
                            @JsonProperty("info") String info) {
        this.role = role;
        this.info = info;
    }

    public ParticipationDTO(long id,
                            Profession role,
                            String info,
                            PersonDTO person,
                            ShowDTO show) {
        this.id = id;
        this.role = role;
        this.info = info;
        this.person = person;
        this.show = show;
    }
}
