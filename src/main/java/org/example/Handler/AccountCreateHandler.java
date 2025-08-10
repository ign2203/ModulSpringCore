package org.example.Handler;

import org.example.Account;
import org.example.AccountService;
import org.example.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Scanner;
@Component
public class AccountCreateHandler implements Account.OperationHandler {
    private static final Logger log = LoggerFactory.getLogger(AccountCreateHandler.class);
    private final Scanner console;
    private final AccountService accountService;

    public AccountCreateHandler(Scanner console, AccountService accountService) {
        this.console = console;
        this.accountService = accountService;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Для создания счета введите ID пользователя");
            String userIdStr = console.nextLine().trim();
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            }catch (NumberFormatException g){
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", g);
                return;
            }
            accountService.createAccount(userId);
            System.out.println("Пользователь успешно создал счет!");
            log.info("Пользователь с ID {} успешно создал счет", userId);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
            log.error("Ошибка при создании счета", e);
        }
    }
    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_CREATE;
    }
}
