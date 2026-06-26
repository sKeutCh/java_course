public class BankAccount {
    private final String ownerName;
    private long balance;

    public BankAccount(String ownerName, long initialBalance) {
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    public void deposit(long amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Внесена сумма: " + amount + ", На балансе: " + balance);
        }
    }

    public boolean withdraw(long amount) {
        if (amount <= 0) {
            System.out.println("Сумма должна быть положительной");
            return false;
        }
        if (amount > balance) {
            System.out.println("Недостаточно средств");
            return false;
        }
        balance -= amount;
        return true;
    }

    public long getBalance() {
        return balance;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
