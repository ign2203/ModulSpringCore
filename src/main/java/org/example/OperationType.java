package org.example;

public enum OperationType {
    USER_CREATE(1, "Создать пользователя"),
    SHOW_ALL_USERS(2, "Показать всех пользователей"),
    ACCOUNT_CREATE(3, "Создать счёт"),
    ACCOUNT_CLOSE(4, "Закрыть указанный счёт"),
    ACCOUNT_DEPOSIT(5, "Пополнить счёт"),
    ACCOUNT_TRANSFER(6, "Перевод средств"),
    ACCOUNT_WITHDRAW(7, "Снять средства"),
    EXIT(0, "Выйти");

    private final int code;
    private final String description;

    OperationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
/*
Я хочу разобраться с этим методом, вчера был вечер и я устрал сейчас с новыми силами готов разобраться!
 */
    public static OperationType fromCode(int code) { // метод fromCode, принимает в качестве аргумента int code - т.е. цифру
        for (OperationType type : OperationType.values()) { // проходится по всем объектам Enum  OperationType
            if (type.getCode() == code) { // если геттер типа = равен значению которой введет пользователь,
                return type; // то возращаем нужный объект enum
            }
        }
        throw new IllegalArgumentException("Неизвестная операция: " + code); // иначе неверный ввод, если пользователь попробует ввести букве или цифру 8
    }
}
