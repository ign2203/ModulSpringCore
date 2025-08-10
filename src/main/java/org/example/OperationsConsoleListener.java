package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {
    private final Scanner console = new Scanner(System.in);
    private final Map<OperationType, Account.OperationHandler> handlers; // вот здесь я так пониманию, что мы создаем Map, которая будет по нужному enum будет выдавать нужный класс реализованный от интерфейса OperationHandler
    private static final Logger log = LoggerFactory.getLogger(OperationsConsoleListener.class);

    public OperationsConsoleListener(List<Account.OperationHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        Account.OperationHandler::getOperationType,
                        Function.identity()
                ));
    }

    public void showMenu() {
        System.out.println("\nВыберите операцию:");
        for (OperationType type : OperationType.values()) {
            System.out.printf("%d. %s%n", type.getCode(), type.getDescription()); //честно скопировал этот код, с сообщения которое ты писала ранее
            // чем System.out.printf отличается от System.out.println() - незнаю
        }
    }

//    private void handleOperation(OperationType operation) {
//        switch (operation) {
//            case USER_CREATE -> {
//                System.out.println("Введите логин пользователя: ");
//                String login = console.nextLine();
//                userService.createUser(login);
//            }
//            case SHOW_ALL_USERS -> {
//                System.out.println("Выводим список всех пользователей: ");
//                userService.getAllUsers();
//            }
//            case ACCOUNT_CREATE -> {
//                System.out.println("Введите Ваш ID (ID пользователя)");
//
//            }
//            case ACCOUNT_CLOSE -> {
//                System.out.println("Для удаления счета,введите Ваш ID (ID пользователя");
//                Long userId = console.nextLong();
//                skipLine();
//                System.out.println("Для удаления счета,введите Ваш ID счета (ID счета)");
//                Long id = console.nextLong();
//                skipLine();
//                accountService.closeAccount(userId, id);
//            }
//            case ACCOUNT_DEPOSIT -> {
//                System.out.println("Для пополнения счета,введите Ваш ID счета (ID счета)");
//                Long id = console.nextLong();
//                skipLine();
//                System.out.println("Для пополнения счета,введите сумму пополнения");
//                BigDecimal amount = console.nextBigDecimal();
//                skipLine();
//                accountService.depositAccount(id, amount);
//            }
//            case ACCOUNT_TRANSFER -> {
//                System.out.println("Для перевода средств, введите Ваш ID счета (ID счет отправителя)");
//                Long fromId = console.nextLong();
//                skipLine();
//                System.out.println("Для перевода средств, введите ID счет получателя (ID счет получателя)");
//                Long toId = console.nextLong();
//                skipLine();
//                System.out.println("Для перевода средств,введите сумму перевода");
//                BigDecimal amount = console.nextBigDecimal();
//                skipLine();
//                accountService.transfer(fromId, toId, amount);
//            }
//            case ACCOUNT_WITHDRAW -> {
//                System.out.println("Для снятия  средств, введите Ваш ID счета (ID счет пользователя)");
//                Long accountId = console.nextLong();
//                skipLine();
//                System.out.println("Для снятия  средств, введите сумму снятия");
//                BigDecimal amount = console.nextBigDecimal();
//                skipLine();
//                accountService.withdrawFromAccount(accountId, amount);
//            }
//            default -> {
//                System.out.println("Операция пока не реализована.");
//            }
//        }
//    }

    public void start() {
        boolean running = true;
        showMenu();

        while (running) {
            System.out.print("Введите номер операции: ");
            String inputStr = console.nextLine().trim(); // читаем всё, убираем пробелы

            if (inputStr.isEmpty()) {
                System.out.println("Вы ничего не ввели. Попробуйте снова.");
                continue; // возвращаемся к меню
            }

            int input;
            try {
                input = Integer.parseInt(inputStr); // конвертация в число
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не число, попробуйте снова.");
                log.error("Ввод некорректных данных (не число)", e);
                continue; // возвращаемся к меню
            }

            try {
                OperationType operation = OperationType.fromCode(input);

                if (operation == OperationType.EXIT) {
                    System.out.println("Выход из программы...");
                    running = false;
                    continue;
                }

                Account.OperationHandler handler = handlers.get(operation);
                if (handler == null) {
                    log.warn("Операция {} пока не реализована", operation);
                    System.out.println("Операция пока не реализована");
                    continue;
                }

                handler.handle();

            } catch (IllegalArgumentException e) {
                System.out.println("Код операции не найден.");
                log.error("Введён несуществующий код операции", e);
            } catch (Exception e) {
                System.out.println("Произошла ошибка при выполнении операции.");
                log.error("Необработанная ошибка", e);
            }

            if (running) {
                showMenu();
            }
        }
    }
}


