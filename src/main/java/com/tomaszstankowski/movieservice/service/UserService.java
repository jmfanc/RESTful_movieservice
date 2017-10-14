package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.entity.UserRole;
import com.tomaszstankowski.movieservice.repository.RatingRepository;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import com.tomaszstankowski.movieservice.service.exception.already_exists.EmailAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.already_exists.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidUserException;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final RatingRepository ratingRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, RatingRepository ratingRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
        this.encoder = encoder;
    }

    public User findOne(String login) {
        return userRepo.findOne(login);
    }

    public Page<User> findAll(int page, Sort sort) {
        return userRepo.findAll(createPageable(page, sort));
    }

    public Page<User> findByName(String name, int page, Sort sort) {
        return userRepo.findUsersByNameContains(name, createPageable(page, sort));
    }

    public Page<Rating> findUserRatings(Specifications<Rating> specs, int page) {
        Pageable pageable = new PageRequest(page, 10, new Sort("date"));
        return ratingRepo.findAll(specs, pageable);
    }

    public void add(User body) {
        validateUser(body);
        if (userRepo.findOne(body.getLogin()) != null)
            throw new UserAlreadyExistsException(body.getLogin());
        User user = new User(
                body.getLogin(),
                encoder.encode(body.getPassword()),
                body.getName(),
                body.getEmail(),
                body.getSex()
        );
        userRepo.save(user);
    }

    public void setRole(String login, UserRole role) {
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        user.setRole(role);
        userRepo.save(user);
    }

    public void edit(User body) {
        User user = userRepo.findOne(body.getLogin());
        if (user == null)
            throw new UserNotFoundException(body.getLogin());
        validateUser(body);
        user.setName(body.getName());
        user.setEmail(body.getEmail());
        user.setSex(body.getSex());
        userRepo.save(user);
    }

    public void remove(String login) {
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        userRepo.delete(login);
    }

    private Pageable createPageable(int page, Sort sort) {
        return new PageRequest(page, 10, sort);
    }

    private void validateUser(User user) {
        if (!isLoginValid(user.getLogin()) || !isPasswordValid(user.getPassword()) || !isEmailValid(user.getEmail()))
            throw new InvalidUserException();
        if (userRepo.findByEmail(user.getEmail()) != null)
            throw new EmailAlreadyExistsException(user.getEmail());
    }

    private boolean isLoginValid(String login) {
        final String loginPattern = "^[0-9A-Za-ząćęłńóśżźĄĆĘŁŃÓŚŻŹ_]{1,40}$";
        Pattern pattern = Pattern.compile(loginPattern);
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return !(password == null || password.isEmpty() || password.length() > 20);
    }

    private boolean isEmailValid(String email) {
        if (email == null)
            return false;
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
