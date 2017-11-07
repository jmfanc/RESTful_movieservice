package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class PersonDTO implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private String name;

    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String birthPlace;

    private Sex sex;

    private Set<Profession> professions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateModified;

    @JsonCreator
    public PersonDTO(@JsonProperty("name") String name,
                     @JsonProperty("birthDate") Date birthDate,
                     @JsonProperty("birthPlace") String birthPlace,
                     @JsonProperty("sex") Sex sex,
                     @JsonProperty("professions") Set<Profession> professions) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.sex = sex;
        this.professions = professions;
    }

    public PersonDTO(long id,
                     String name,
                     Date birthDate,
                     String birthPlace,
                     Sex sex,
                     Set<Profession> professions,
                     Date dateAdded,
                     Date dateModified) {
        this(name, birthDate, birthPlace, sex, professions);
        this.id = id;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }
}
