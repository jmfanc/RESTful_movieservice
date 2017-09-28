package com.tomaszstankowski.movieservice.model.dto;

import com.tomaszstankowski.movieservice.model.*;

import javax.lang.model.type.UnknownTypeException;

public class ModelMapper {

    public User fromDTO(UserDTO dto) {
        return new User(
                dto.getLogin(),
                dto.getName(),
                dto.getMail(),
                dto.getSex());
    }

    public UserDTO fromEntity(User entity) {
        UserDTO dto = new UserDTO(
                entity.getLogin(),
                entity.getName(),
                entity.getMail(),
                entity.getSex());
        dto.setJoined(entity.getJoined());
        return dto;
    }

    public Person fromDTO(PersonDTO dto) {
        Person entity = new Person(
                dto.getName(),
                dto.getBirthDate(),
                dto.getBirthPlace(),
                dto.getSex()
        );
        entity.getProfessions().addAll(dto.getProfessions());
        return entity;
    }

    public PersonDTO fromEntity(Person entity) {
        PersonDTO dto = new PersonDTO(
                entity.getName(),
                entity.getBirthDate(),
                entity.getBirthPlace(),
                entity.getSex()
        );
        dto.setId(entity.getId());
        dto.getProfessions().addAll(entity.getProfessions());
        return dto;
    }

    public ShowDTO fromEntity(Show entity) {
        if (entity instanceof Movie) {
            return fromEntity((Movie) entity);
        }
        if (entity instanceof Serial) {
            return fromEntity((Serial) entity);
        }
        throw new UnknownTypeException(null, null);
    }

    public Movie fromDTO(MovieDTO dto) {
        Movie entity = new Movie(
                dto.getTitle(),
                dto.getDescription(),
                dto.getReleaseDate(),
                dto.getLocation(),
                dto.getDuration(),
                dto.getBoxoffice()
        );
        dto.getGenres().stream()
                .map(Genre::new)
                .forEach(entity.getGenres()::add);
        return entity;
    }

    public Serial fromDTO(SerialDTO dto) {
        Serial entity = new Serial(
                dto.getTitle(),
                dto.getDescription(),
                dto.getReleaseDate(),
                dto.getLocation(),
                dto.getSeasons()
        );
        dto.getGenres().stream()
                .map(Genre::new)
                .forEach(entity.getGenres()::add);
        return entity;
    }

    public MovieDTO fromEntity(Movie entity) {
        MovieDTO dto = new MovieDTO(
                entity.getTitle(),
                entity.getDescription(),
                entity.getReleaseDate(),
                entity.getLocation(),
                entity.getDuration(),
                entity.getBoxoffice()
        );
        dto.setId(entity.getId());
        entity.getGenres().stream()
                .map(Genre::getName)
                .forEach(dto.getGenres()::add);
        return dto;
    }

    public SerialDTO fromEntity(Serial entity) {
        SerialDTO dto = new SerialDTO(
                entity.getTitle(),
                entity.getDescription(),
                entity.getReleaseDate(),
                entity.getLocation(),
                entity.getSeasons()
        );
        dto.setId(entity.getId());
        entity.getGenres().stream()
                .map(Genre::getName)
                .forEach(dto.getGenres()::add);
        return dto;
    }
}
