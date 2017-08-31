package com.tomaszstankowski.movieservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(of = "login")
@Entity(name = "USERS")
public class User implements Serializable {

    @Id
    private String login;

    private String name;

    private String mail;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date joined = new Date();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
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
