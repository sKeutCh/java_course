package org.example;

import org.example.controller.ATM;
import org.example.repository.UserRepository;
import org.example.service.AccountService;
import org.example.service.UserService;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        AccountService accountService = new AccountService();

        ATM atm = new ATM(userService, accountService);
        atm.start();
    }
}