package org.example.service;

import org.example.domain.BankAccount;
import org.example.domain.CreditAccount;
import org.example.domain.DebitAccount;
import org.example.domain.User;
import org.example.domain.enums.AccountType;
import org.example.exception.AccountNotFoundException;
import org.example.exception.NotEnoughMoneyException;

import java.util.List;
import java.util.UUID;

public class AccountService {

    public BankAccount open(User user, AccountType accountType) {
        String accountNumber = generateAccountNumber();
        BankAccount account;

        if (accountType == AccountType.DEBIT) {
            account = new DebitAccount(accountNumber);
        } else {
            account = new CreditAccount(accountNumber);
        }

        user.addAccount(account);
        return account;
    }

    public void deposit(User user, String accountNumber, long amount)
            throws AccountNotFoundException, NotEnoughMoneyException {
        BankAccount account = findAccount(user, accountNumber);
        account.deposit(amount);
    }

    public void withdraw(User user, String accountNumber, long amount)
            throws AccountNotFoundException, NotEnoughMoneyException {
        BankAccount account = findAccount(user, accountNumber);
        account.withdraw(amount);
    }

    public void transfer(User fromUser, String fromAccountNumber,
                         User toUser, String toAccountNumber, long amount)
            throws AccountNotFoundException, NotEnoughMoneyException {
        BankAccount fromAccount = findAccount(fromUser, fromAccountNumber);
        BankAccount toAccount = findAccount(toUser, toAccountNumber);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    public long getBalance(List<? extends BankAccount> accounts) {
        long total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }

    public long getAvailableBalance(List<? extends BankAccount> accounts) {
        long total = 0;
        for (BankAccount account : accounts) {
            total += account.getAvailableBalance();
        }
        return total;
    }

    private BankAccount findAccount(User user, String accountNumber)
            throws AccountNotFoundException {
        for (BankAccount account : user.getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        throw new AccountNotFoundException("Account with number '" + accountNumber + "' not found");
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}