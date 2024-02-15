package com.example.chomg.data;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return this.username;
    }

}