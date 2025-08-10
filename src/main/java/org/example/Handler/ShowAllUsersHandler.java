package org.example.Handler;

import org.example.Account;
import org.example.OperationType;
import org.example.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ShowAllUsersHandler implements Account.OperationHandler {
    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ShowAllUsersHandler.class);


    public ShowAllUsersHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Выводим список всех пользователей: ");
            userService.getAllUsers();
            System.out.flush();
            log.info("Операция по выводу всех пользователей выполнено успешно");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
            System.out.flush();
            log.error("Ошибка при выполнении операции вывода всех пользователей", e);
        }
    }
    @Override
    public OperationType getOperationType() {
        return OperationType.SHOW_ALL_USERS;
    }
}
