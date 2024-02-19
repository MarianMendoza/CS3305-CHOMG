package com.example.chomg.userinterface;

public class ChangePasswordRequest {
    private String newPassword;
    private String confirmPassword;

    // Constructor
    public ChangePasswordRequest(String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters
    public String getNewPassword() {
        return newPassword;
    }

    // Setters
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
