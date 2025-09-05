package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id // здесь все понятно
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login")
    private String login;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accountList = new ArrayList<>();

    public User(String login) {
        this.login = login;

    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User() {

    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(
            List<Account> accountList) {
        this.accountList = accountList;
    }

    public void addAccount(Account account) {
        accountList.add(account);
        account.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", accounts=" + (accountList != null ?
                accountList.stream().map(Account::getId).toList() : null) +
                '}';
    }

    public Long getId() {
        return id;
    }
}


