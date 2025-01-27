package com.example.eventy.users.model;

import java.util.List;

public class UpgradeProfile {
    private String email;
    private String accountType; // "EVENT ORGANIZER" || "SOLUTIONS PROVIDER"
    private String firstName; // null if provider
    private String lastName; // null if provider
    private String companyName; // null if organizer
    private String description; // null if organizer
    private List<String> profilePictures;

    public UpgradeProfile() {}

    public UpgradeProfile(String email, String accountType, String firstName, String lastName, String companyName, String description, List<String> profilePictures) {
        this.email = email;
        this.accountType = accountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.description = description;
        this.profilePictures = profilePictures;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<String> profilePictures) {
        this.profilePictures = profilePictures;
    }
}
