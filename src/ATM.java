import java.util.Scanner;

public class ATM {
    private BankAccount account;
    private Scanner scanner;

    public ATM(BankAccount account) {
        this.account = account;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("1. Показать баланс");
            System.out.println("2. Внести деньги");
            System.out.println("3. Снять деньги");
            System.out.println("4. Выход");
            System.out.print("Выберите операцию: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Ваш баланс: " + account.getBalance());
                    break;
                case 2:
                    System.out.print("Сколько вы хотите внести денег? ");
                    long depositAmount = scanner.nextLong();
                    account.deposit(depositAmount);
                    break;
                case 3:
                    System.out.print("Сколько вы хотите снять денег? ");
                    long withdrawAmount = scanner.nextLong();
                    if (account.withdraw(withdrawAmount)) {
                        dispenseMoney(withdrawAmount);
                    }
                    break;
                case 4:
                    System.out.println("Приходите еще :)");
                    return;
                default:
                    System.out.println("Неверная операция");
            }
        }
    }

    public void dispenseMoney(long amount) {
        long remaining = amount; //содаем остаток - постепенно «откусывать» от неё выданные купюры
        System.out.println("Выдача денег:");
        for (Denomination d : Denomination.values()) { //for-each — «для каждого номинала d из всех номиналов».
            int count = (int) (remaining / d.getValue());//подсчет кол-ва купюр;
            if (count > 0) { //проверка прячет лишние строки из вывода (1000 = 0 и тд)
                System.out.println(d.getValue() + " x " + count);
                remaining -= (long) count * d.getValue();//уменьшаем остаток
            }//приведение к long, чтобы при больших суммах не было переполнения int.
        }
    }
}
