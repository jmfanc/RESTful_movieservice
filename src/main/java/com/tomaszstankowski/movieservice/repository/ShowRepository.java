package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

public interface ShowRepository extends JpaRepository<Show, Long>, JpaSpecificationExecutor<Show> {

    public Show findByTitleAndReleaseDate(String title, Date releaseDate);
}
