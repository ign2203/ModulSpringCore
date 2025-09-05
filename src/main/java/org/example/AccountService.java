package org.example;


import jakarta.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
public class AccountService {

    private final TransactionHelper transactionHelper;
    private SessionFactory sessionFactory;
    private final UserService userService;

    @Value("${account.default.amount}")
    private BigDecimal defaultAmount;
    @Value("${account.transfer.commission}")
    private BigDecimal commissionPercent;

    @PostConstruct
    public void checkProps() {
        System.out.println("Default Amount: '" + defaultAmount + "'");
        System.out.println("Commission: '" + commissionPercent + "'");
    }

    public AccountService(SessionFactory sessionFactory, TransactionHelper transactionHelper, UserService userService) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.userService = userService;
    }

    public Account createAccount(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            User user = session.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
            }

            Account account = new Account(defaultAmount);
            account.setUser(user);
            user.getAccountList().add(account);
            session.persist(account);
            return account;
        });
    }


    public void closeAccount(Long userId, Long id) {
        transactionHelper.executeInTransaction(session -> {
            User user = session.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
            }
            Account account = session.find(Account.class, id);
            if (account == null) {
                throw new IllegalArgumentException("Счет с ID " + id + " не найден");
            }
            List<Account> userAccounts = session.createQuery("FROM Account a  WHERE a.user.id = :userId", Account.class)
                    .setParameter("userId", userId)
                    .list();
            if (userAccounts.size() <= 1) {
                throw new IllegalArgumentException("Нельзя закрыть единственный счёт");
            }

            boolean accountBelongsToUser = userAccounts
                    .stream()
                    .anyMatch(a -> a.getId().equals(id));

            if (!accountBelongsToUser) {
                throw new IllegalArgumentException("Счёт с ID " + id + " не принадлежит пользователю");
            }
            int index = userAccounts.indexOf(account);
            Account targetAccount;
            if (index == 0) {
                targetAccount = userAccounts.get(1);
            } else {
                targetAccount = userAccounts.get(0);
            }
            if (account.getMoneyAmount().compareTo(BigDecimal.ZERO) > 0) {
                targetAccount.deposit(account.getMoneyAmount());
            }
            session.remove(account);
        });
    }

    public Account depositAccount(long accountId, BigDecimal amount) {
        return transactionHelper.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);

            if (account == null) {
                throw new IllegalArgumentException("Счет с ID " + accountId + " не найден");
            }
            account.deposit(amount);
            System.out.println("Пополнение: " + amount + " на счёт " + accountId);
            session.merge(account);
            return account;
        });
    }

    public Account withdrawFromAccount(long accountId, BigDecimal amount) {
        return transactionHelper.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Счет с ID " + accountId + " не найден");
            }
            account.withdraw(amount);
            System.out.println("Снятие: " + amount + " со счёта " + accountId);
            return account;
        });
    }

    public Account transfer(Long fromId, Long toId, BigDecimal amount) {
        return transactionHelper.executeInTransaction(session -> {
            Account fromAccount = session.find(Account.class, fromId);
            Account toAccount = session.find(Account.class, toId);
            if (fromAccount == null) {
                throw new IllegalArgumentException("Счёт списания с таким ID не существует");
            }
            if (toAccount == null) {
                throw new IllegalArgumentException("Счёта пополнения с таким ID не существует");
            }
            if (fromId.equals(toId)) {
                throw new IllegalArgumentException("Нельзя перевести средства самому себе");
            }

            User fromUser = session.createQuery("SELECT u FROM User u JOIN u.accountList a WHERE a.id = :fromId", User.class)
                    .setParameter("fromId", fromId)
                    .uniqueResult();
            User toUser = session.createQuery("SELECT u FROM User u JOIN u.accountList a WHERE a.id = :toId", User.class)
                    .setParameter("toId", toId)
                    .uniqueResult();

            if (fromUser.getId().equals(toUser.getId()) || commissionPercent.compareTo(BigDecimal.ZERO) == 0) {
                if (fromAccount.getMoneyAmount().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Недостаточно средств на балансе отправителя");
                }
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
                System.out.println("Перевод выполнен: " + amount + " со счёта " + fromId + " на счёт " + toId);
                System.out.println("Баланс получателя: " + toAccount.getMoneyAmount());
            } else {
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
//            throw new RuntimeException("Тестовое исключение: проверка rollback");
            return fromAccount;
        });
    }
}