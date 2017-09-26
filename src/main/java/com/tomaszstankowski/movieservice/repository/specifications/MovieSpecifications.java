package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.Movie;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {
    public static Specification<Movie> titleContains(String phrase) {
        return (root, query, builder) -> ShowPredicates.titleContains(phrase, root, query, builder);
    }

    public static Specification<Movie> olderThan(int year) {
        return (root, query, builder) -> ShowPredicates.olderThan(year, root, query, builder);
    }

    public static Specification<Movie> youngerThan(int year) {
        return (root, query, builder) -> ShowPredicates.youngerThan(year, root, query, builder);
    }

    public static Specification<Movie> hasAtLeastOneGenre(String[] genres) {
        return ((root, query, builder) -> ShowPredicates.hasAtLeastOneGenre(genres, root, query, builder));
    }

    public static Specification<Movie> shorterThan(int duration) {
        return (root, query, builder) -> builder.lessThan(root.<Short>get("duration"), Short.valueOf((short) duration));

    }

    public static Specification<Movie> longerThan(int duration) {
        return (root, query, builder) -> builder.greaterThan(root.<Short>get("duration"), Short.valueOf((short) duration));

    }
}
