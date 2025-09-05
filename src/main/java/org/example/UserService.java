package org.example;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;


@Service
public class UserService {


    private SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public UserService(SessionFactory sessionFactory, TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    @Value("${account.default.amount}")
    private BigDecimal defaultAmount;

    public User createUser(String login) {
        User existingUser = transactionHelper.executeInTransaction(session -> {
            return session.createQuery("FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResult();
        });
        if (existingUser != null) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }
        User user = new User();
        user.setLogin(login);
        Account account = new Account(defaultAmount);
        account.setUser(user);
        user.getAccountList().add(account);
        transactionHelper.executeInTransaction(session -> {
            session.persist(user);
            session.persist(account);
        });
        return user;
    }


    public User findUserById(Long userId) {
        User existingUser = transactionHelper.executeInTransaction(session -> {
            return session.find(User.class, userId);
        });

        if (existingUser == null) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        return existingUser;
    }

    public List<User> getAllUsers() {
        List<User> users = transactionHelper.executeInTransaction(session -> {
            return session.createQuery("SELECT u FROM User u", User.class)
                    .list();
        });
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }
}



