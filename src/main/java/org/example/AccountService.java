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
    private BigDecimal commissionPercent;

    public AccountService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(Long userId) {
        long id = nextAccountId.getAndIncrement();
        Account account = new Account(id, userId, defaultAmount);
        if (accountsById.containsKey(id)) {
            throw new IllegalArgumentException("Счёт с таким ID уже существует");
        }
        accountsById.put(id, account);
        accountsByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(account);
        User user = userService.findUserById(userId);
        user.getAccountList().add(account);

        return account;
    }

    public Account closeAccount(Long userId, Long id) {

        if (!accountsById.containsKey(id)) {
            throw new IllegalArgumentException("Счёт с таким ID не существует");
        }
        List<Account> userAccounts = accountsByUser.get(userId);
        if (userAccounts == null || userAccounts.size() <= 1) {
            throw new IllegalArgumentException("Нельзя закрыть единственный счёт");
        }
        Account accountToClose = accountsById.get(id);
        int index = userAccounts.indexOf(accountToClose);
        Account targetAccount;
        if (index == 0) {
            targetAccount = userAccounts.get(1);
        } else {
            targetAccount = userAccounts.get(0);
        }
        if (accountToClose.getMoneyAmount().compareTo(BigDecimal.ZERO) > 0) {
            targetAccount.deposit(accountToClose.getMoneyAmount());
        }
        accountsById.remove(id);
        userAccounts.remove(accountToClose);
        System.out.println("Счёт " + id + " закрыт. Средства переведены на счёт " + targetAccount.getId());
        User user = userService.findUserById(userId);
        user.getAccountList().remove(accountToClose);

        return accountToClose;
    }

    public Account depositAccount(long accountId, BigDecimal amount) {
        if (!accountsById.containsKey(accountId)) {
            throw new IllegalArgumentException("Счёт с таким ID не существует");
        }
        Account account = accountsById.get(accountId);
        account.deposit(amount);
        System.out.println("Пополнение: " + amount + " на счёт " + accountId);
        return account;
    }

    public Account withdrawFromAccount(long accountId, BigDecimal amount) {
        if (!accountsById.containsKey(accountId)) {
            throw new IllegalArgumentException("Счёт с таким ID не существует");
        }
        Account account = accountsById.get(accountId);
        account.withdraw(amount);
        System.out.println("Снятие: " + amount + " со счёта " + accountId);
        return account;

    }


    public Account transfer(long fromId, long toId, BigDecimal amount) {
        if (!accountsById.containsKey(fromId)) {
            throw new IllegalArgumentException("Счёт списания с таким ID не существует");
        }
        if (!accountsById.containsKey(toId)) {
            throw new IllegalArgumentException("Счёта пополнения с таким ID не существует");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { //Проверка суммы перевода:
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        Account fromAccount = accountsById.get(fromId);
        Account toAccount = accountsById.get(toId);

        if (fromId == toId) {
            throw new IllegalArgumentException("Нельзя перевести средства самому себе");
        }

        if (fromAccount.getUserId() == toAccount.getUserId() || commissionPercent.compareTo(BigDecimal.ZERO) == 0) {
            if (fromAccount.getMoneyAmount().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Недостаточно средств на балансе отправителя");
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


    @PostConstruct
    public void checkProps() {
        System.out.println("Default Amount: '" + defaultAmount + "'");
        System.out.println("Commission: '" + commissionPercent + "'");
    }

}
