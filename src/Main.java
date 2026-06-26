public class Main {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("Ulyana", 1000);
        ATM atm = new ATM(account);
        atm.showMenu();
    }
}