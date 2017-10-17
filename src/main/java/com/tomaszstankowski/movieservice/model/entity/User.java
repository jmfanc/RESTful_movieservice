package com.tomaszstankowski.movieservice.model.entity;

import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.model.enums.UserRole;
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

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Temporal(TemporalType.DATE)
    private Date joined = new Date();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    UserRole role = UserRole.USER;

    public User() {
    }

    public User(String login, String password, String name, String email, Sex sex) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.sex = sex;
    }
}
