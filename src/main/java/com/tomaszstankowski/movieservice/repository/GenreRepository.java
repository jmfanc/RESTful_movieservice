package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GenreRepository extends JpaRepository<Genre, String> {
}
