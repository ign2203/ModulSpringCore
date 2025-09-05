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

    public static OperationType fromCode(int code) {
        for (OperationType type : OperationType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестная операция: " + code);
    }
}
