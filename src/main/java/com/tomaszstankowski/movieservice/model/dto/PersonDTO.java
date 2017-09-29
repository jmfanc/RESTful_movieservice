package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class PersonDTO implements Serializable {

    private long id;

    private String name;

    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String birthPlace;

    private Sex sex;

    private Set<Profession> professions = new HashSet<>();

    public PersonDTO() {
    }

    public PersonDTO(String name, Date birthDate, String birthPlace, Sex sex) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
    }
}
