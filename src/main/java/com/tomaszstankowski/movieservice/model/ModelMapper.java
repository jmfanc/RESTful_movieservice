package com.tomaszstankowski.movieservice.model;

import com.tomaszstankowski.movieservice.model.dto.*;
import com.tomaszstankowski.movieservice.model.entity.*;

import javax.lang.model.type.UnknownTypeException;

public class ModelMapper {

    public User fromDTO(UserDTO dto) {
        if (dto == null)
            return null;
        return new User(
                dto.getLogin(),
                dto.getName(),
                dto.getMail(),
                dto.getSex());
    }

    public UserDTO fromEntity(User entity) {
        if (entity == null)
            return null;
        UserDTO dto = new UserDTO(
                entity.getLogin(),
                entity.getName(),
                entity.getMail(),
                entity.getSex());
        dto.setJoined(entity.getJoined());
        return dto;
    }

    public Person fromDTO(PersonDTO dto) {
        if (dto == null)
            return null;
        Person entity = new Person(
                dto.getName(),
                dto.getBirthDate(),
                dto.getBirthPlace(),
                dto.getSex()
        );
        entity.getProfessions().addAll(dto.getProfessions());
        entity.setId(dto.getId());
        return entity;
    }

    public PersonDTO fromEntity(Person entity) {
        if (entity == null)
            return null;
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

    public Show fromDTO(ShowDTO dto) {
        if (dto == null)
            return null;
        if (dto instanceof MovieDTO)
            return fromDTO((MovieDTO) dto);
        if (dto instanceof SerialDTO)
            return fromDTO((SerialDTO) dto);
        throw new UnknownTypeException(null, null);
    }

    public ShowDTO fromEntity(Show entity) {
        if (entity == null)
            return null;
        if (entity instanceof Movie)
            return fromEntity((Movie) entity);
        if (entity instanceof Serial)
            return fromEntity((Serial) entity);
        throw new UnknownTypeException(null, null);
    }

    public Movie fromDTO(MovieDTO dto) {
        if (dto == null)
            return null;
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
        entity.setId(dto.getId());
        return entity;
    }

    public Serial fromDTO(SerialDTO dto) {
        if (dto == null)
            return null;
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
        entity.setId(dto.getId());
        return entity;
    }

    public MovieDTO fromEntity(Movie entity) {
        if (entity == null)
            return null;
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
        if (entity == null)
            return null;
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

    public Participation fromDTO(ParticipationDTO dto) {
        if (dto == null)
            return null;
        Participation entity = new Participation(
                dto.getRole(),
                dto.getInfo(),
                fromDTO(dto.getPerson()),
                fromDTO(dto.getShow())
        );
        entity.setId(dto.getId());
        return entity;
    }

    public ParticipationDTO fromEntity(Participation entity) {
        if (entity == null)
            return null;
        ParticipationDTO dto = new ParticipationDTO(
                entity.getRole(),
                entity.getInfo(),
                fromEntity(entity.getPerson()),
                fromEntity(entity.getShow())
        );
        dto.setId(entity.getId());
        return dto;
    }
}
