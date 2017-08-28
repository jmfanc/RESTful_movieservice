package com.tomaszstankowski.movieservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "PEOPLE")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person implements Serializable {
    @Id
    private final UUID id = UUID.randomUUID();
    @NonNull
    private String name;
    @Column(name = "BIRTH_DATE")
    private Date birthDate;
    @Column(name = "BIRTH_PLACE")
    private String birthPlace;
    @Enumerated(EnumType.STRING)
    private Sex sex;
}
