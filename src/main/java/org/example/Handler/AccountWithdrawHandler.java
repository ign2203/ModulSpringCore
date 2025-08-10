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
public class AccountWithdrawHandler implements Account.OperationHandler {
    private final AccountService accountService;
    private final Scanner console;
    private static final Logger log = LoggerFactory.getLogger(AccountWithdrawHandler.class);


    public AccountWithdrawHandler(AccountService accountService, Scanner console) {
        this.accountService = accountService;
        this.console = console;
    }

    @Override
    public void handle() {
        try {
            System.out.println("Для снятия  средств, введите Ваш ID счета ");
            String accountIdStr = console.nextLine().trim();
            long accountId;
            try {
                accountId = Long.parseLong(accountIdStr);
            } catch (NumberFormatException g) {
            System.out.println("Ошибка: ID должен быть числом.");
            log.error("Некорректный ввод ID счёта", g);
            return;
        }
            System.out.println("Для снятия  средств, введите сумму снятия");
            String amountStr = console.nextLine().trim(); // amountStr для ввода
            BigDecimal amount; // для операции
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: сумма должна быть числом.");
                log.error("Некорректный ввод суммы", e);
                return;
            }
            accountService.withdrawFromAccount(accountId, amount);
            System.out.println("Вы успешно сняли средства со счета" + accountId + " в количестве: " + amount);
            log.info("Вы успешно сняли средства со счета {}  в количестве: {}", accountId, amount);
        } catch (IllegalArgumentException ex) {
            System.out.println("Ошибка " + ex.getMessage());
            log.error("Ошибка при выполнении операции снятия средств", ex);
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ACCOUNT_WITHDRAW;
    }
}
