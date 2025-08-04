package com.example.eventy.users.model;

import java.util.List;

public class RegisterData {
    private List<String> profilePictures;
    private String email;
    private String password;
    private String confirmedPassword;
    private String firstName;
    private String lastName;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;

    public RegisterData() {

    }

    public RegisterData(List<String> profilePictures, String email, String password, String confirmedPassword, String firstName, String lastName, String name, String description, String address, String phoneNumber) {
        this.profilePictures = profilePictures;
        this.email = email;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public List<String> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<String> profilePictures) {
        this.profilePictures = profilePictures;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

