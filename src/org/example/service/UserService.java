package org.example.service;

import org.example.domain.User;
import org.example.exception.InvalidPasswordException;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserLoginNotFoundException;
import org.example.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String login, String password) throws UserAlreadyExistsException {
        if (login == null || login.trim().isEmpty()) {
            throw new UserAlreadyExistsException("Login cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new UserAlreadyExistsException("Password cannot be empty");
        }

        User user = new User(login, password);
        userRepository.save(user);
        return user;
    }

    public User login(String login, String password)
            throws UserLoginNotFoundException, InvalidPasswordException {
        User user = userRepository.findByLogin(login);

        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("Invalid password for user '" + login + "'");
        }

        return user;
    }

    public boolean userExists(String login) {
        return userRepository.existsByLogin(login);
    }
}