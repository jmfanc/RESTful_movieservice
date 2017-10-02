package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.Show;
import com.tomaszstankowski.movieservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {

    Rating findByUserAndShow(User user, Show show);

    @Query(value = "SELECT AVG(RATING) FROM RATINGS WHERE SHOW_ID = ?1 ", nativeQuery = true)
    Float getAverageRatingForShow(long id);


    long countByShow(Show show);
}
