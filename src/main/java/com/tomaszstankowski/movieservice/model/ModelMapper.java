package com.tomaszstankowski.movieservice.model;

import com.tomaszstankowski.movieservice.model.dto.*;
import com.tomaszstankowski.movieservice.model.entity.*;

import javax.lang.model.type.UnknownTypeException;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelMapper {

    public User fromDTO(UserDTO dto) {
        return (dto == null) ? null : new User(
                dto.getLogin(),
                dto.getPassword(),
                dto.getName(),
                dto.getEmail(),
                dto.getSex());
    }

    public UserDTO fromEntity(User entity) {
        return (entity == null) ? null : new UserDTO(
                entity.getLogin(),
                entity.getName(),
                entity.getSex(),
                entity.getJoined());
    }

    public Person fromDTO(PersonDTO dto) {
        if (dto == null)
            return null;
        Person person = new Person(
                dto.getName(),
                dto.getBirthDate(),
                dto.getBirthPlace(),
                dto.getSex());
        person.getProfessions().addAll(dto.getProfessions());
        return person;
    }

    public PersonDTO fromEntity(Person entity) {
        return (entity == null) ? null : new PersonDTO(
                entity.getId(),
                entity.getName(),
                entity.getBirthDate(),
                entity.getBirthPlace(),
                entity.getSex(),
                entity.getProfessions(),
                entity.getDateAdded(),
                entity.getDateModified()
        );
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
        Movie movie = new Movie(
                dto.getTitle(),
                dto.getDescription(),
                dto.getDateReleased(),
                dto.getLocation(),
                dto.getDuration(),
                dto.getBoxoffice()
        );
        dto.getGenres().stream()
                .map(Genre::new)
                .forEach(movie.getGenres()::add);
        return movie;
    }

    public Serial fromDTO(SerialDTO dto) {
        if (dto == null)
            return null;
        Serial serial = new Serial(
                dto.getTitle(),
                dto.getDescription(),
                dto.getDateReleased(),
                dto.getLocation(),
                dto.getSeasons()
        );
        dto.getGenres().stream()
                .map(Genre::new)
                .forEach(serial.getGenres()::add);
        return serial;
    }

    public MovieDTO fromEntity(Movie entity) {
        if (entity == null)
            return null;
        Set<String> genres = entity.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        return new MovieDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateReleased(),
                entity.getLocation(),
                genres,
                0f,
                0,
                entity.getDateAdded(),
                entity.getDateModified(),
                entity.getDuration(),
                entity.getBoxoffice()
        );
    }

    public SerialDTO fromEntity(Serial entity) {
        if (entity == null)
            return null;
        Set<String> genres = entity.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        return new SerialDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateReleased(),
                entity.getLocation(),
                genres,
                0f,
                0,
                entity.getDateAdded(),
                entity.getDateModified(),
                entity.getSeasons()
        );
    }

    public Participation fromDTO(ParticipationDTO dto) {
        return (dto == null) ? null : new Participation(
                dto.getRole(),
                dto.getInfo(),
                fromDTO(dto.getPerson()),
                fromDTO(dto.getShow())
        );
    }

    public ParticipationDTO fromEntity(Participation entity) {
        return (entity == null) ? null : new ParticipationDTO(
                entity.getId(),
                entity.getRole(),
                entity.getInfo(),
                fromEntity(entity.getPerson()),
                fromEntity(entity.getShow())
        );
    }

    public RatingDTO fromEntity(Rating entity) {
        return (entity == null) ? null : new RatingDTO(
                entity.getId(),
                entity.getRating(),
                entity.getDate(),
                fromEntity(entity.getShow()),
                fromEntity(entity.getUser())
        );
    }
}
