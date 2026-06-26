public class Main {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("Ulyana", 1000);//объявляем переменную account типа BankAccount, это ссылка на объект.
        //создаём новый объект в памяти, вызывается конструктор с параметрами имя = "Ulyana", начальный баланс = 1000.
        ATM atm = new ATM(account);
        //создаём новый объект банкомата, в конструктор передаём уже созданный счёт account.
        atm.showMenu();//запуск бесконечного цикла меню
    }
}