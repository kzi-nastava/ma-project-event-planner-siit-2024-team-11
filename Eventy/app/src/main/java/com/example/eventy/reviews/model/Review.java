package com.example.eventy.reviews.model;

import com.example.eventy.model.enums.Status;

public class Review {
    private Long id;
    private String comment;
    private Integer grade;
    private String senderEmail;
    private String recipientEmail;
    private String title; // event/product/service name
    private Status status;
    private Boolean isDeleted;
    private String senderName;
    private String senderAvatar;

    public Review() {

    }

    public Review(Long id, String comment, Integer grade, String senderEmail, String recipientEmail, String title, Status status, Boolean isDeleted) {
        this.id = id;
        this.comment = comment;
        this.grade = grade;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
}
