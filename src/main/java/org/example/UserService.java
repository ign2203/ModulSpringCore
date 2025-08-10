package org.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class UserService {
    private final AtomicInteger userIdSequence = new AtomicInteger(1);
    private final Map<Long, User> usersById = new HashMap<>(); // библиотека для быстрого поиска и удаления по id пользователя


    public User createUser(String login) {  // метод создания пользователя
        // Проверяем, нет ли уже пользователя с таким логином
        boolean loginExists = usersById.values().stream()// разберись с кодом
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(login)); // разберись с кодом
        if (loginExists) { // разберись с кодом
            throw new IllegalArgumentException("Пользователь с таким логином уже существует"); // разберись с кодом
        }


        long id = userIdSequence.getAndIncrement();
        User user = new User(id, login, new ArrayList<>()); // вот здесь интересно, я думаю accountList нам не нужен, это отдельный метод для создания счета, нужно подумать, здесь что то не так
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



