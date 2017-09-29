package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String mail;
    private Sex sex;
    @JsonFormat(timezone = "Europe/Paris", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date joined;

    public UserDTO() {
    }

    public UserDTO(String login, String name, String mail, Sex sex) {
        this.login = login;
        this.name = name;
        this.mail = mail;
        this.sex = sex;
    }
}
