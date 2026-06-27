package org.example.domain;

import org.example.domain.enums.AccountType;
import org.example.exception.NotEnoughMoneyException;

public class DebitAccount extends BankAccount {

    public DebitAccount(String accountNumber) {
        super(accountNumber);
        this.balance = 0;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.DEBIT;
    }

    @Override
    public void deposit(long amount) throws NotEnoughMoneyException {
        if (amount <= 0) {
            throw new NotEnoughMoneyException("Deposit amount must be positive");
        }
        balance += amount;
    }

    @Override
    public void withdraw(long amount) throws NotEnoughMoneyException {
        if (amount <= 0) {
            throw new NotEnoughMoneyException("Withdraw amount must be positive");
        }
        if (amount > balance) {
            throw new NotEnoughMoneyException("Not enough money on debit account");
        }
        balance -= amount;
    }

    @Override
    public long getAvailableBalance() {
        return balance;
    }
}