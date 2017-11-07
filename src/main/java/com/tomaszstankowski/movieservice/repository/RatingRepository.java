package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.Show;
import com.tomaszstankowski.movieservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {

    Rating findByUserAndShow(User user, Show show);
}
