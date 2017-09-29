package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.entity.Serial;
import org.springframework.data.jpa.domain.Specification;

public class SerialSpecifications {
    public static Specification<Serial> titleContains(String phrase) {
        return (root, query, builder) -> ShowPredicates.titleContains(phrase, root, query, builder);
    }

    public static Specification<Serial> olderThan(int year) {
        return (root, query, builder) -> ShowPredicates.olderThan(year, root, query, builder);
    }

    public static Specification<Serial> youngerThan(int year) {
        return (root, query, builder) -> ShowPredicates.youngerThan(year, root, query, builder);
    }

    public static Specification<Serial> hasAtLeastOneGenre(String[] genres) {
        return ((root, query, builder) -> ShowPredicates.hasAtLeastOneGenre(genres, root, query, builder));
    }

    public static Specification<Serial> shorterThan(int seasons) {
        return (root, query, builder) -> builder.lessThan(root.<Short>get("seasons"), Short.valueOf((short) seasons));

    }

    public static Specification<Serial> longerThan(int seasons) {
        return (root, query, builder) -> builder.greaterThan(root.<Short>get("seasons"), Short.valueOf((short) seasons));

    }
}
