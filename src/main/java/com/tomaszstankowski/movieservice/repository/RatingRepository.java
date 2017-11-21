package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.Show;
import com.tomaszstankowski.movieservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {

    Rating findByUserAndShow(User user, Show show);

    @Query("select distinct r from ratings r where r.user in :#{#user.followed} order by r.date desc")
    Page<Rating> findUserFollowedRatings(@Param("user") User user, Pageable pageable);

    @Query("select distinct r from ratings r where r.show = :#{#show} and r.user in :#{#user.followed} order by r.date desc")
    List<Rating> findUserFollowedRatings(@Param("user") User user, @Param("show") Show show);
}
