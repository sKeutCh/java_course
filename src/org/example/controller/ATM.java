package org.example.controller;

import org.example.domain.BankAccount;
import org.example.domain.User;
import org.example.domain.enums.AccountType;
import org.example.domain.enums.Denomination;
import org.example.exception.*;
import org.example.service.AccountService;
import org.example.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ATM {
    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner;
    private User currentUser;

    public ATM(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Welcome to ATM ===");
            System.out.println("1. Sign In");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        signIn();
                        break;
                    case 2:
                        signUp();
                        break;
                    case 3:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void signIn() {
        try {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            currentUser = userService.login(login, password);
            System.out.println("Welcome, " + currentUser.getLogin() + "!");
            showATMMenu();
        } catch (UserLoginNotFoundException | InvalidPasswordException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void signUp() {
        try {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            currentUser = userService.register(login, password);
            System.out.println("Registration successful! Welcome, " + currentUser.getLogin() + "!");
            showATMMenu();
        } catch (UserAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showATMMenu() {
        while (currentUser != null) {
            System.out.println("\n--- ATM Menu ---");
            System.out.println("1. View all accounts balance");
            System.out.println("2. Open new account");
            System.out.println("3. Deposit money");
            System.out.println("4. Withdraw money");
            System.out.println("5. Transfer money");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllBalances();
                        break;
                    case 2:
                        openAccount();
                        break;
                    case 3:
                        depositMoney();
                        break;
                    case 4:
                        withdrawMoney();
                        break;
                    case 5:
                        transferMoney();
                        break;
                    case 6:
                        System.out.println("Logged out successfully.");
                        currentUser = null;
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void viewAllBalances() {
        List<BankAccount> accounts = currentUser.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet.");
            return;
        }

        System.out.println("\n=== Your Accounts ===");
        for (BankAccount account : accounts) {
            System.out.printf("Account: %s | Type: %s | Balance: %d | Available: %d%n",
                    account.getAccountNumber(),
                    account.getAccountType().getDescription(),
                    account.getBalance(),
                    account.getAvailableBalance());
        }

        long totalBalance = accountService.getBalance(accounts);
        long totalAvailable = accountService.getAvailableBalance(accounts);
        System.out.printf("%nTotal Balance: %d | Total Available: %d%n", totalBalance, totalAvailable);
    }

    private void openAccount() {
        System.out.println("Select account type:");
        System.out.println("1. Debit Account");
        System.out.println("2. Credit Account");
        System.out.print("Choose: ");

        try {
            int typeChoice = Integer.parseInt(scanner.nextLine());
            AccountType accountType;

            if (typeChoice == 1) {
                accountType = AccountType.DEBIT;
            } else if (typeChoice == 2) {
                accountType = AccountType.CREDIT;
            } else {
                System.out.println("Invalid option.");
                return;
            }

            BankAccount account = accountService.open(currentUser, accountType);
            System.out.println("Account opened successfully!");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Type: " + account.getAccountType().getDescription());
            System.out.println("Initial Balance: " + account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void depositMoney() {
        try {
            String accountNumber = selectAccount("deposit to");
            if (accountNumber == null) return;

            System.out.print("Amount to deposit: ");
            long amount = Long.parseLong(scanner.nextLine());

            accountService.deposit(currentUser, accountNumber, amount);
            System.out.println("Deposited successfully!");
            dispenseMoney(amount);
        } catch (AccountNotFoundException | NotEnoughMoneyException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void withdrawMoney() {
        try {
            String accountNumber = selectAccount("withdraw from");
            if (accountNumber == null) return;

            System.out.print("Amount to withdraw: ");
            long amount = Long.parseLong(scanner.nextLine());

            accountService.withdraw(currentUser, accountNumber, amount);
            System.out.println("Withdrawn successfully!");
            dispenseMoney(amount);
        } catch (AccountNotFoundException | NotEnoughMoneyException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void transferMoney() {
        try {
            String fromAccountNumber = selectAccount("transfer from");
            if (fromAccountNumber == null) return;

            System.out.print("Recipient login: ");
            String toLogin = scanner.nextLine();

            User toUser = userService.login(toLogin, "dummy");
            String toAccountNumber = selectRecipientAccount(toUser);
            if (toAccountNumber == null) return;

            System.out.print("Amount to transfer: ");
            long amount = Long.parseLong(scanner.nextLine());

            accountService.transfer(currentUser, fromAccountNumber, toUser, toAccountNumber, amount);
            System.out.println("Transfer successful!");
        } catch (UserLoginNotFoundException | InvalidPasswordException e) {
            System.out.println("Error: Recipient not found.");
        } catch (AccountNotFoundException | NotEnoughMoneyException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private String selectAccount(String action) {
        List<BankAccount> accounts = currentUser.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts. Please open one first.");
            return null;
        }

        System.out.println("\nSelect account to " + action + ":");
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount acc = accounts.get(i);
            System.out.printf("%d. %s (%s) - Balance: %d%n",
                    i + 1, acc.getAccountNumber(), acc.getAccountType().getDescription(), acc.getBalance());
        }
        System.out.print("Choose: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > accounts.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return accounts.get(choice - 1).getAccountNumber();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private String selectRecipientAccount(User user) {
        List<BankAccount> accounts = user.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Recipient has no accounts.");
            return null;
        }

        System.out.println("\nSelect recipient's account:");
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount acc = accounts.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, acc.getAccountNumber(), acc.getAccountType().getDescription());
        }
        System.out.print("Choose: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > accounts.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return accounts.get(choice - 1).getAccountNumber();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private void dispenseMoney(long amount) {
        long remaining = amount;
        System.out.println("Dispensing:");
        for (Denomination d : Denomination.values()) {
            int count = (int) (remaining / d.getValue());
            if (count > 0) {
                System.out.println(d.getValue() + " x " + count);
                remaining -= (long) count * d.getValue();
            }
        }
    }
}