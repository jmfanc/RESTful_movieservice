package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class RatingSpecifications {

    public static Specification<Rating> youngerThan(Date date) {
        return (root, query, builder) -> builder.greaterThan(root.<Date>get("date"), date);
    }

    public static Specification<Rating> user(String login) {
        return (root, query, builder) -> builder.equal(root.<User>get("user").<String>get("login"), login);
    }

    public static Specification<Rating> rating(short rating) {
        return (root, query, builder) -> builder.equal(root.<Short>get("rating"), rating);
    }
}
