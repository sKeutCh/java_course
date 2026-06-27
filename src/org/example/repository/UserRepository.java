package org.example.repository;

import org.example.domain.User;
import org.example.exception.UserLoginNotFoundException;
import org.example.exception.UserAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<String, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public void save(User user) throws UserAlreadyExistsException {
        if (users.containsKey(user.getLogin())) {
            throw new UserAlreadyExistsException("User with login '" + user.getLogin() + "' already exists");
        }
        users.put(user.getLogin(), user);
    }

    public User findByLogin(String login) throws UserLoginNotFoundException {
        User user = users.get(login);
        if (user == null) {
            throw new UserLoginNotFoundException("User with login '" + login + "' not found");
        }
        return user;
    }

    public boolean existsByLogin(String login) {
        return users.containsKey(login);
    }
}