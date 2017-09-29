package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Page<User> findUsersByNameContains(String name, Pageable pageable);
}
