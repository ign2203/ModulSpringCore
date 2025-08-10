package org.example;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountService {
    private final AtomicLong nextAccountId = new AtomicLong(1);
    private final UserService userService;
    private final Map<Long, Account> accountsById = new HashMap<>();
    private final Map<Long, List<Account>> accountsByUser = new HashMap<>();

    @Value("${account.default.amount}")
    private BigDecimal defaultAmount;
    @Value("${account.transfer.commission}")
    private BigDecimal commissionPercent; // здесь наверное не правильно указан тип,  потому что commissionPercent - это процент

    public AccountService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(Long userId) { // метод создания счета
        long id = nextAccountId.getAndIncrement();
        Account account = new Account(id, userId, defaultAmount);
        if (accountsById.containsKey(id)) { // здесь я поставил !, если
            throw new IllegalArgumentException("Счёт с таким ID уже существует");
        }
        accountsById.put(id, account);
        accountsByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(account);
        User user = userService.findUserById(userId);
        user.getAccountList().add(account);

        return account;
    }

    public Account closeAccount(Long userId, Long id) {

        if (!accountsById.containsKey(id)) {  // Проверка: существует ли счёт с таким ID
            throw new IllegalArgumentException("Счёт с таким ID не существует");
        }
        List<Account> userAccounts = accountsByUser.get(userId);  // Получаем все счета пользователя
        if (userAccounts == null || userAccounts.size() <= 1) {// Нельзя закрыть единственный счёт
            throw new IllegalArgumentException("Нельзя закрыть единственный счёт");
        }
        Account accountToClose = accountsById.get(id); // Находим счёт, который нужно закрыть
        int index = userAccounts.indexOf(accountToClose);// Определяем его индекс в списке счетов indexOf
        Account targetAccount;  // Определяем, куда перевести деньги со счёта
        if (index == 0) {// Если закрываемый счёт — первый, переводим на второй
            targetAccount = userAccounts.get(1);
        } else {
            targetAccount = userAccounts.get(0);       // Иначе — переводим на первый
        }
        if (accountToClose.getMoneyAmount().compareTo(BigDecimal.ZERO) > 0) { // Если на счете были деньги — переводим на выбранный
            targetAccount.deposit(accountToClose.getMoneyAmount());
        }
        accountsById.remove(id);         // Удаляем счёт из списка и из мапы
        userAccounts.remove(accountToClose);
        System.out.println("Счёт " + id + " закрыт. Средства переведены на счёт " + targetAccount.getId());
        User user = userService.findUserById(userId);
        user.getAccountList().remove(accountToClose);

        return accountToClose; //       // Возвращаем удалённый счёт
    }

    public Account depositAccount(long accountId, BigDecimal amount) {
        if (!accountsById.containsKey(accountId)) { //Если счёт не найден — выбрось исключение.
            throw new IllegalArgumentException("Счёт с таким ID не существует");
        }
        Account account = accountsById.get(accountId);//Найди счёт в accountsById по accountId.
        account.deposit(amount);// Вызови deposit(amount) у найденного счёта.
        System.out.println("Пополнение: " + amount + " на счёт " + accountId);
        return account;
    }

    public Account withdrawFromAccount(long accountId, BigDecimal amount) {
        if (!accountsById.containsKey(accountId)) {
            throw new IllegalArgumentException("Счёт с таким ID не существует"); // Если не найден — бросить исключение
        }
        Account account = accountsById.get(accountId); // Найти счёт в accountsById
        account.withdraw(amount); // Вызвать у него withdraw(amount)
        System.out.println("Снятие: " + amount + " со счёта " + accountId);
        return account;

    }


    public Account transfer(long fromId, long toId, BigDecimal amount) {
        if (!accountsById.containsKey(fromId)) {
            throw new IllegalArgumentException("Счёт списания с таким ID не существует"); // Проверка существования обоих счетов:
        }
        if (!accountsById.containsKey(toId)) {
            throw new IllegalArgumentException("Счёта пополнения с таким ID не существует"); // Проверка существования обоих счетов:
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { //Проверка суммы перевода:
            throw new IllegalArgumentException("Сумма перевода должна быть положительной"); //amount <= 0 → выбросить исключение
        }

        Account fromAccount = accountsById.get(fromId); //Определяем владельцев счетов:
        Account toAccount = accountsById.get(toId); // Определяем владельцев счетов:

        if (fromId == toId) {
            throw new IllegalArgumentException("Нельзя перевести средства самому себе");
        }

        if (fromAccount.getUserId() == toAccount.getUserId() || commissionPercent.compareTo(BigDecimal.ZERO) == 0) { // // Перевод между своими счетами — без комиссии
            if (fromAccount.getMoneyAmount().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Недостаточно средств на балансе отправителя");// Проверка: хватает ли денег на перевод
            }
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            System.out.println("Перевод выполнен: " + amount + " со счёта " + fromId + " на счёт " + toId);
            System.out.println("Баланс получателя: " + toAccount.getMoneyAmount());
        } else {  // Перевод между разными пользователями — с комиссией
            BigDecimal commission = amount.multiply(commissionPercent).divide(BigDecimal.valueOf(100));
            BigDecimal total = amount.add(commission);
            if (fromAccount.getMoneyAmount().compareTo(total) < 0) {
                throw new IllegalArgumentException("Недостаточно средств с учётом комиссии");
            }
            fromAccount.withdraw(total);
            toAccount.deposit(amount);
            System.out.println("Перевод выполнен: " + amount + " со счёта " + fromId + " на счёт " + toId);
            System.out.println("Баланс получателя: " + toAccount.getMoneyAmount());
        }
        return fromAccount;
    }

    /*

И пусть он:
Проходит по accountsByUser.entrySet(). Я не помню что делает метод entrySet()
Для каждого userId выводит все аккаунты
Использует System.out.println(...)
Мой ответ:
Нужно еще написать условие, для того случая если у нас библиотека еще не создана, и в ней нет аккаунтов

 */
//    public void showAllUsersWithAccounts() {
//        if (accountsById.isEmpty()) {
//            System.out.println("Нет зарегистрированных пользователей и счетов.");
//            return;
//        }
//        for (Map.Entry<Long, List<Account>> entry : accountsByUser.entrySet()) {
//            System.out.println("ID пользователя: " + entry.getKey());
//            for (Account account : entry.getValue()) {
//                System.out.println("  → " + account);
//            }
//        }
//        /*
//        Что делает entry.getKey()?
//Мой ответ: выдает ключи из мап библиотеки entry, т.е. из перебора
//Что возвращает entry.getValue()?
//Мой ответ:  возвращает значение а именно все значения переменных объекта  account,
//Почему мы используем вложенный цикл for (Account account : entry.getValue())?
//Мой ответ:  мы сперва нашли все ключи,а после по каждому ключу вывели значения этого ключа
//         */
//
//    }

    @PostConstruct
    public void checkProps() {
        System.out.println("Default Amount: '" + defaultAmount + "'");
        System.out.println("Commission: '" + commissionPercent + "'");
    }

}











