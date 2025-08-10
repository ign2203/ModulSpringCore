package org.example;

import java.util.List;

public class User {
   private final Long id;
    private  final String login;
    private List<Account> accountList; //Все счета, привязанные к пользователю
    public User(Long id, String login, List<Account> accountsList) {
        this.id = id;
        this.login = login;
        this.accountList = accountsList;
    }
    public Long getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    @Override
    public String toString() {
        return "\nUser (Пользователь) {" +
                "\n  id пользователя = " + id +
                ",\n  логин = '" + login + '\'' +
                ",\n  список счетов = " + accountList +
                "\n}";
    }





    /*



 */
}


