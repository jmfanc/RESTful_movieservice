package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import com.tomaszstankowski.movieservice.service.exception.InvalidUserException;
import com.tomaszstankowski.movieservice.service.exception.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.UserNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User find(String login) {
        return userRepo.findOne(login);
    }

    public List<User> findAll(Sort sort) {
        return userRepo.findAll(sort);
    }

    public List<User> findAll(String name, Sort sort) {
        return userRepo.findUsersByNameContains(name, sort);
    }

    public void add(User body) {
        validateUser(body);
        if (userRepo.findOne(body.getLogin()) != null)
            throw new UserAlreadyExistsException(body.getLogin());
        User user = new User(
                body.getLogin(),
                body.getName(),
                body.getMail(),
                body.getSex()
        );
        userRepo.save(user);
    }

    public void edit(String login, User body) {
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        validateUser(body);
        user.setName(body.getName());
        user.setMail(body.getMail());
        user.setSex(body.getSex());
        userRepo.save(user);
    }

    public void delete(String login) {
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        userRepo.delete(login);
    }

    private void validateUser(User user) {
        if (user.getLogin() == null
                || user.getLogin().isEmpty()
                || user.getMail() == null
                || user.getMail().isEmpty())
            throw new InvalidUserException();
    }
}
