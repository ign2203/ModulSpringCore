package org.example.Handler;

import org.example.Account;
import org.example.AccountService;
import org.example.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountDepositHandler implements Account.OperationHandler {
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AccountDepositHandler.class);
    private final Scanner console;

    public AccountDepositHandler(AccountService accountService, Scanner console) {
        this.accountService = accountService;
        this.console = console;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Для пополнения счета, введите Ваш ID счета:");
            String idStr = console.nextLine().trim();
            long id;
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", e);
                return;
            }

            System.out.println("Введите сумму пополнения:");
            String amountStr = console.nextLine().trim();
            BigDecimal amount; // для операции
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: сумма должна быть числом.");
                log.error("Некорректный ввод суммы", e);
                return;
            }

            accountService.depositAccount(id, amount);
            System.out.println("Пользователь с ID " + id + " успешно пополнил счет на сумму: " + amount);
            log.info("Пользователь с ID {} успешно пополнил счет на {}", id, amount);

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
            log.error("Ошибка при пополнении счета", e);
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_DEPOSIT;
    }
}
