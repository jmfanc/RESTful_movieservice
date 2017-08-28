package com.tomaszstankowski.movieservice.repository;

import com.tomaszstankowski.movieservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    public List<User> findUsersByNameContains(String name);
}
