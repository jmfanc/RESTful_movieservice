package com.tomaszstankowski.movieservice.model.entity;

import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(of = "login")
@Entity(name = "USERS")
public class User {

    @Id
    private String login;

    private String name;

    private String mail;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Temporal(TemporalType.DATE)
    private Date joined = new Date();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

    public User() {
    }

    public User(String login, String name, String mail, Sex sex) {
        this.login = login;
        this.name = name;
        this.mail = mail;
        this.sex = sex;
    }
}
