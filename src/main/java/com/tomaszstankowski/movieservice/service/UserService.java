package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.model.enums.UserRole;
import com.tomaszstankowski.movieservice.repository.RatingRepository;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import com.tomaszstankowski.movieservice.service.exception.conflict.EmailAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.conflict.SelfFollowException;
import com.tomaszstankowski.movieservice.service.exception.conflict.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.conflict.UserAlreadyFollowedException;
import com.tomaszstankowski.movieservice.service.exception.not_found.FollowerNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.ImmutableAdministratorException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidUserException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.SingleAdministratorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
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
        setAdmin();
    }

    private void setAdmin() {
        User admin = new User(
                "admin",
                encoder.encode("admin"),
                "Tomasz Stankowski",
                "example@gmail.com",
                Sex.MALE
        );
        admin.setRole(UserRole.ADMIN);
        if (userRepo.findOne(admin.getLogin()) == null)
            userRepo.save(admin);
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
        if (userRepo.findByEmail(body.getEmail()) != null)
            throw new EmailAlreadyExistsException(body.getEmail());
        User user = new User(
                body.getLogin(),
                encoder.encode(body.getPassword()),
                body.getName(),
                body.getEmail(),
                body.getSex()
        );
        userRepo.save(user);
    }

    public void edit(User body) {
        validateUser(body);
        User user = userRepo.findOne(body.getLogin());
        if (user == null)
            throw new UserNotFoundException(body.getLogin());
        if (!body.getEmail().equals(user.getEmail()))
            if (userRepo.findByEmail(body.getEmail()) != null)
                throw new EmailAlreadyExistsException(body.getEmail());
        user.setName(body.getName());
        user.setEmail(body.getEmail());
        user.setSex(body.getSex());
        userRepo.save(user);
    }

    public List<User> getFollowers(String username) {
        User user = userRepo.findOne(username);
        if (user == null)
            throw new UserNotFoundException(username);
        return user.getFollowers();
    }

    public List<User> getFollowed(String username) {
        User user = userRepo.findOne(username);
        if (user == null)
            throw new UserNotFoundException(username);
        return user.getFollowed();
    }

    public void addFollower(String followedName, String followerName) {
        if (followedName.equals(followerName))
            throw new SelfFollowException();
        User followed = userRepo.findOne(followedName);
        if (followed == null)
            throw new UserNotFoundException(followedName);
        User follower = userRepo.findOne(followerName);
        if (follower == null)
            throw new UserNotFoundException(followerName);
        if (followed.getFollowers().contains(follower))
            throw new UserAlreadyFollowedException(followedName, followerName);
        followed.getFollowers().add(follower);
        follower.getFollowed().add(followed);
        userRepo.save(followed);
    }

    public void removeFollower(String followedName, String followerName) {
        User followed = userRepo.findOne(followedName);
        if (followed == null)
            throw new UserNotFoundException(followedName);
        User follower = userRepo.findOne(followerName);
        if (follower == null)
            throw new UserNotFoundException(followerName);
        if (!followed.getFollowers().contains(follower))
            throw new FollowerNotFoundException(followedName, followerName);
        followed.getFollowers().remove(follower);
        follower.getFollowed().remove(followed);
        userRepo.save(followed);
    }

    public void changeRole(String login, UserRole role) {
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        if (user.getRole().equals(UserRole.ADMIN))
            throw new ImmutableAdministratorException();
        if (role.equals(UserRole.ADMIN))
            throw new SingleAdministratorException();
        user.setRole(role);
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
