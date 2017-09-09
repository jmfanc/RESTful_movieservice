package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID>, JpaSpecificationExecutor<Show> {

    public Show findByTitleAndReleaseDate(String title, Date releaseDate);
}
