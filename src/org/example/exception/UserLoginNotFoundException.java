package org.example.exception;

public class UserLoginNotFoundException extends Exception {
    public UserLoginNotFoundException(String message) {
        super(message);
    }
}