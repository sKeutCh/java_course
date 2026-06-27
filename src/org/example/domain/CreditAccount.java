package org.example.domain;

import org.example.domain.enums.AccountType;
import org.example.exception.NotEnoughMoneyException;

public class CreditAccount extends BankAccount {
    private final long creditLine;

    public CreditAccount(String accountNumber) {
        super(accountNumber);
        this.creditLine = 20000;
        this.balance = creditLine;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CREDIT;
    }

    @Override
    public void deposit(long amount) throws NotEnoughMoneyException {
        if (amount <= 0) {
            throw new NotEnoughMoneyException("Deposit amount must be positive");
        }
        balance += amount;
        if (balance > creditLine) {
            balance = creditLine;
        }
    }

    @Override
    public void withdraw(long amount) throws NotEnoughMoneyException {
        if (amount <= 0) {
            throw new NotEnoughMoneyException("Withdraw amount must be positive");
        }
        if (amount > balance) {
            throw new NotEnoughMoneyException("Not enough money on credit account");
        }
        balance -= amount;
    }

    @Override
    public long getAvailableBalance() {
        return balance;
    }
}