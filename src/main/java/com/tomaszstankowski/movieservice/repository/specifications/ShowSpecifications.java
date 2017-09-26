package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.Show;
import org.springframework.data.jpa.domain.Specification;
public class ShowSpecifications {

    public static Specification<Show> titleContains(String phrase) {
        return (root, query, builder) -> ShowPredicates.titleContains(phrase, root, query, builder);
    }

    public static Specification<Show> olderThan(int year) {
        return (root, query, builder) -> ShowPredicates.olderThan(year, root, query, builder);
    }

    public static Specification<Show> youngerThan(int year) {
        return (root, query, builder) -> ShowPredicates.youngerThan(year, root, query, builder);
    }

    public static Specification<Show> hasAtLeastOneGenre(String[] genres) {
        return ((root, query, builder) -> ShowPredicates.hasAtLeastOneGenre(genres, root, query, builder));
    }
}
