package org.example;

import org.example.Handler.OperationHandler;
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
    private final Scanner console;
    private final Map<OperationType, OperationHandler> handlers;
    private static final Logger log = LoggerFactory.getLogger(OperationsConsoleListener.class);

    public OperationsConsoleListener(List<OperationHandler> handlerList, Scanner scanner) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        OperationHandler::getOperationType,
                        Function.identity()
                ));
        this.console = scanner;
    }

    public void start() {
        boolean running = true;
        showMenu();

        while (running) {
            System.out.print("Введите номер операции: ");
            String inputStr = console.nextLine().trim();

            if (inputStr.isEmpty()) {
                System.out.println("Вы ничего не ввели. Попробуйте снова.");
                continue;
            }

            int input;
            try {
                input = Integer.parseInt(inputStr);
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не число, попробуйте снова.");
                log.error("Ввод некорректных данных (не число)", e);
                continue;
            }

            try {
                OperationType operation = OperationType.fromCode(input);

                if (operation == OperationType.EXIT) {
                    System.out.println("Выход из программы...");
                    running = false;
                    continue;
                }

                OperationHandler handler = handlers.get(operation);
                if (handler == null) {
                    log.warn("Операция {} пока не реализована", operation);
                    System.out.println("Операция пока не реализована");
                    continue;
                }
                handler.handle();

                if (operation != OperationType.EXIT) {
                    showMenu(); // показываем меню снова
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Код операции не найден.");
                log.error("Введён несуществующий код операции", e);
            } catch (Exception e) {
                System.out.println("Произошла ошибка при выполнении операции.");
                log.error("Необработанная ошибка", e);
            }
        }

    }

    private void showMenu() {
        System.out.println("\nВыберите операцию:");
        for (OperationType type : OperationType.values()) {
            System.out.printf("%d. %s%n", type.getCode(), type.getDescription());

        }
    }
}


