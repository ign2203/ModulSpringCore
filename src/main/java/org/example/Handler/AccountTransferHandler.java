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
public class AccountTransferHandler implements Account.OperationHandler {
    private final AccountService accountService;
    private final Scanner console;
    private static final Logger log = LoggerFactory.getLogger(AccountTransferHandler.class);

    public AccountTransferHandler(AccountService accountService, Scanner console) {
        this.accountService = accountService;
        this.console = console;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Для перевода средств, введите Ваш ID счета (ID счет отправителя)");
            String fromIdStr = console.nextLine().trim();
            long fromId;
            try {
                fromId = Long.parseLong(fromIdStr);
            } catch (NumberFormatException g) {
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", g);
                return;
            }
            System.out.println("Для перевода средств, введите ID счет получателя (ID счет получателя)");
            String toIdStr = console.nextLine().trim();
            long toId;
            try {
                toId = Long.parseLong(toIdStr);
            } catch (NumberFormatException g) {
                System.out.println("Ошибка: ID должен быть числом.");
                log.error("Некорректный ввод ID счёта", g);
                return;
            }
            System.out.println("Для перевода средств,введите сумму перевода");
            String amountStr = console.nextLine().trim();
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: сумма должна быть числом.");
                log.error("Некорректный ввод суммы", e);
                return;
            }
            accountService.transfer(fromId, toId, amount);
            System.out.println("Пользователь с ID-" + fromId + " перевел средства " + amount + " на счет отправителя ID-" + toId);
            log.info("Пользователь с ID {} перевел средства {} на счет отправителя {} ", fromId, amount, toId);
        } catch (IllegalArgumentException b) {
            System.out.println("Ошибка " + b.getMessage());
            log.error("Ошибка при переводе средств", b);
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_TRANSFER;
    }
}
