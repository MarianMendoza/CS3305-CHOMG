package com.example.chomg.data;

public class User {
    private String username;
    private String password;
    private String token;

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public String getEmail() {
        return this.username;
    }

}