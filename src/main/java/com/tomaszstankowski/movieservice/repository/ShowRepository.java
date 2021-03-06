package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

public interface ShowRepository extends JpaRepository<Show, Long>, JpaSpecificationExecutor<Show> {

    Show findByTitleAndDateReleased(String title, Date dateReleased);
}
