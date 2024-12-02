package com.example.eventy.users.model;

import java.util.ArrayList;

public class User {
    private UserType accountType;
    private ArrayList<String> profilePictures;
    private String email;
    private String address;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String name;
    private String description;

    public User() {

    }

    public User(UserType accountType, ArrayList<String> profilePictures, String email, String address, String phoneNumber, String firstName, String lastName, String name, String description) {
        this.accountType = accountType;
        this.profilePictures = profilePictures;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.description = description;
    }

    public UserType getAccountType() {
        return accountType;
    }

    public void setAccountType(UserType accountType) {
        this.accountType = accountType;
    }

    public ArrayList<String> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(ArrayList<String> profilePictures) {
        this.profilePictures = profilePictures;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
