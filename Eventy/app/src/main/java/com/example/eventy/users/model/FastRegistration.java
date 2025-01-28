package com.example.eventy.users.model;

public class FastRegistration {
    private String encryptedEmail;
    private String password;
    private String confirmedPassword;
    private String address;
    private String phoneNumber;

    public FastRegistration() {}

    public FastRegistration(String encryptedEmail, String password, String confirmedPassword, String address, String phoneNumber) {
        this.encryptedEmail = encryptedEmail;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getEncryptedEmail() {
        return encryptedEmail;
    }

    public void setEncryptedEmail(String encryptedEmail) {
        this.encryptedEmail = encryptedEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
