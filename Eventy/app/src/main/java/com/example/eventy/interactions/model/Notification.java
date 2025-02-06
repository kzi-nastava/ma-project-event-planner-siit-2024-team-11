package com.example.eventy.interactions.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private NotificationType type;
    private Integer redirectionId;
    private String title;
    private String message;
    private String graderImage;
    private String graderEmail;
    private Integer grade;
    private LocalDateTime timestamp;

    public Notification() {}

    public Notification(Long id, NotificationType type, Integer redirectionId, String title, String message, String graderImage, String graderEmail, Integer grade, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.redirectionId = redirectionId;
        this.title = title;
        this.message = message;
        this.graderImage = graderImage;
        this.graderEmail = graderEmail;
        this.grade = grade;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Integer getRedirectionId() {
        return redirectionId;
    }

    public void setRedirectionId(Integer redirectionId) {
        this.redirectionId = redirectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGraderImage() {
        return graderImage;
    }

    public void setGraderImage(String graderImage) {
        this.graderImage = graderImage;
    }

    public String getGraderEmail() {
        return graderEmail;
    }

    public void setGraderEmail(String graderEmail) {
        this.graderEmail = graderEmail;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "id=" + id +
                ", type=" + type +
                ", redirectionId=" + redirectionId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", graderImage='" + graderImage + '\'' +
                ", graderFullName='" + graderEmail + '\'' +
                ", grade=" + grade +
                ", timestamp=" + timestamp +
                '}';
    }
}
