package org.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class UserService {
    private final AtomicInteger userIdSequence = new AtomicInteger(1);
    private final Map<Long, User> usersById = new HashMap<>();

    public User createUser(String login) {

        boolean loginExists = usersById.values().stream()
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
        if (loginExists) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }


        long id = userIdSequence.getAndIncrement();
        User user = new User(id, login, new ArrayList<>());
        if (usersById.containsKey(id)) {
            throw new IllegalArgumentException("Пользователь с таким ID уже существует");
        }
        usersById.put(id, user);
        return user;
    }

    public User findUserById(Long userId) {
        User user = usersById.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        return user;
    }

    public void getAllUsers() {
        if (usersById.isEmpty()) {
            throw new IllegalArgumentException("Нет зарегистрированных пользователей и счетов.");
        }
        for (Map.Entry<Long, User> entry : usersById.entrySet()) {
            System.out.println("ID пользователя: " + entry.getKey());
            User user = entry.getValue();
            System.out.println("  → " + user);
        }
    }
}



