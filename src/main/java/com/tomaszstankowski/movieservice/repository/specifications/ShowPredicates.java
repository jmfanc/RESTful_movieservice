package com.tomaszstankowski.movieservice.repository.specifications;

import com.tomaszstankowski.movieservice.model.entity.Genre;
import com.tomaszstankowski.movieservice.model.entity.Show;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Root and CriteriaQuery cannot be specified explicitly to <Show> because Predicates below are used to create
 * Movie and Serial Specifications as well.
 **/
class ShowPredicates {

    static Predicate titleContains(String phrase, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        return builder.like(root.<String>get("title"), "%" + phrase + "%");
    }

    static Predicate olderThan(int year, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        Date date = new GregorianCalendar(year, 1, 1).getTime();
        return builder.lessThan(root.<Date>get("dateReleased"), date);
    }

    static Predicate youngerThan(int year, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        Date date = new GregorianCalendar(year, 1, 1).getTime();
        return builder.greaterThan(root.<Date>get("dateReleased"), date);
    }

    static Predicate hasAtLeastOneGenre(String[] genres, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        query.distinct(true);
        Join<Show, Genre> showGenreJoin = root.join("genres");
        return showGenreJoin.get("name").in(genres);
    }
}
