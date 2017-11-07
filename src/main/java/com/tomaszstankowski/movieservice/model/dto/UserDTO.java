package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "login")
public class UserDTO implements Serializable {

    private String login;

    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Sex sex;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateJoined;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int followersCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int followedCount;

    @JsonCreator
    public UserDTO(@JsonProperty("login") String login,
                   @JsonProperty("name") String name,
                   @JsonProperty("email") String email,
                   @JsonProperty("sex") Sex sex,
                   @JsonProperty("password") String password) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.sex = sex;
        this.password = password;
    }

    public UserDTO(String login,
                   String name,
                   Sex sex,
                   Date dateJoined,
                   int followersCount,
                   int followedCount) {
        this.login = login;
        this.name = name;
        this.sex = sex;
        this.dateJoined = dateJoined;
        this.followersCount = followersCount;
        this.followedCount = followedCount;
    }
}
