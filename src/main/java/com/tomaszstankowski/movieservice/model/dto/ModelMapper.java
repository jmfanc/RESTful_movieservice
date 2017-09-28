package com.tomaszstankowski.movieservice.model.dto;

import com.tomaszstankowski.movieservice.model.User;

public class ModelMapper {

    public User fromDTO(UserDTO userDTO) {
        return new User(
                userDTO.getLogin(),
                userDTO.getName(),
                userDTO.getMail(),
                userDTO.getSex());
    }

    public UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO(
                user.getLogin(),
                user.getName(),
                user.getMail(),
                user.getSex());
        dto.setJoined(user.getJoined());
        return dto;
    }
}
