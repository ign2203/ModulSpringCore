package org.example;

import java.math.BigDecimal;


public class Account {
    private final Long id; // уникальный номер счета
    private final Long userId;// Идентификатор пользователя, владельца счета. Внешнний ключ
    private BigDecimal moneyAmount; // текущий баланс счета

    // нужно создать конструктор
    // в этом методе зависимость

    public Account(long id, long userId, BigDecimal moneyAmount) {
        this.id = id;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    public void withdraw(BigDecimal amount) { // снятие со счёта
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (moneyAmount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на счете");
        }
        moneyAmount = moneyAmount.subtract(amount);
    }

    public void deposit(BigDecimal amount) { // пополнение счёта
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        moneyAmount = moneyAmount.add(amount);
    }

    public long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", moneyAmount=" + moneyAmount +
                '}';
    }

    public static interface OperationHandler {
        void handle();
        OperationType getOperationType();
    }
}
