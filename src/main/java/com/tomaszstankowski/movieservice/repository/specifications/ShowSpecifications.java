package com.tomaszstankowski.movieservice.repository.specifications;


import com.tomaszstankowski.movieservice.model.Show;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.GregorianCalendar;

public class ShowSpecifications {

    public static Specification<Show> titleContains(String phrase) {
        return (root, query, builder) -> builder.like(root.<String>get("title"), "%" + phrase + "%");
    }

    public static Specification<Show> olderThan(int year) {
        Date date = new GregorianCalendar(year, 1, 1).getTime();
        return (root, query, builder) -> builder.lessThan(root.<Date>get("releaseDate"), date);
    }

    public static Specification<Show> youngerThan(int year) {
        Date date = new GregorianCalendar(year, 12, 31).getTime();
        return (root, query, builder) -> builder.greaterThan(root.<Date>get("releaseDate"), date);
    }
}
