package org.example.Handler;

import org.example.Account;
import org.example.OperationType;
import org.example.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Scanner;


@Component
public class UserCreateHandler implements Account.OperationHandler {
    private static final Logger log = LoggerFactory.getLogger(UserCreateHandler.class);
    private final Scanner console;
    private final UserService userService;

    public UserCreateHandler(Scanner console, UserService userService) {
        this.console = console;
        this.userService = userService;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Введите логин пользователя: ");
            String login = console.nextLine();
            userService.createUser(login);
            System.out.println("Пользователь создан успешно!");
            log.info("Пользователь с логином '{}' создан успешно", login);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            log.error("Ошибка при создании пользователя", e); // логгирование обязательно? IDEA ругается на log
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.USER_CREATE;
    }
}
