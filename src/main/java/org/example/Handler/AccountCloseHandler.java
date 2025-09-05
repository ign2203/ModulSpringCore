package org.example.Handler;

import org.example.AccountService;
import org.example.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCloseHandler implements OperationHandler {
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AccountCloseHandler.class);
    private final Scanner console;

    public AccountCloseHandler(AccountService accountService, Scanner console) {
        this.accountService = accountService;
        this.console = console;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Для удаления счета,введите Ваш ID пользователя");
            String userIdStr = console.nextLine().trim();
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException g) {
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", g);
                return;
            }

            System.out.println("Для удаления счета,введите Ваш ID счета ");
            String idStr = console.nextLine().trim();
            Long id;
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException g) {
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", g);
                return;
            }
            accountService.closeAccount(userId, id);
            System.out.println("Пользователь успешно удалил счет!");
            log.info("Пользователь с ID {} успешно удалил счет {}", userId, id);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
            log.error("Ошибка при удалении счета", e);
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_CLOSE;
    }
}
