package org.example.domain;

import org.example.domain.enums.AccountType;
import org.example.exception.NotEnoughMoneyException;

public abstract class BankAccount {
    protected String accountNumber;
    protected long balance;

    public BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0;
    }

    public abstract AccountType getAccountType();

    public abstract void deposit(long amount) throws NotEnoughMoneyException;

    public abstract void withdraw(long amount) throws NotEnoughMoneyException;

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getBalance() {
        return balance;
    }

    public abstract long getAvailableBalance();
}