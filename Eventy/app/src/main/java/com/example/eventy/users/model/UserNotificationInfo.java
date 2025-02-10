package com.example.eventy.users.model;

import java.time.LocalDateTime;

public class UserNotificationInfo {
    private Long userId;
    private Boolean areNotificationsMuted;
    private LocalDateTime lastReadNotifications;
    private Boolean hasNewNotifications;

    public UserNotificationInfo() {}

    public UserNotificationInfo(Long userId, Boolean areNotificationsMuted, LocalDateTime lastReadNotifications, Boolean hasNewNotifications) {
        this.userId = userId;
        this.areNotificationsMuted = areNotificationsMuted;
        this.lastReadNotifications = lastReadNotifications;
        this.hasNewNotifications = hasNewNotifications;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getAreNotificationsMuted() {
        return areNotificationsMuted;
    }

    public void setAreNotificationsMuted(Boolean areNotificationsMuted) {
        this.areNotificationsMuted = areNotificationsMuted;
    }

    public LocalDateTime getLastReadNotifications() {
        return lastReadNotifications;
    }

    public void setLastReadNotifications(LocalDateTime lastReadNotifications) {
        this.lastReadNotifications = lastReadNotifications;
    }

    public Boolean getHasNewNotifications() {
        return hasNewNotifications;
    }

    public void setHasNewNotifications(Boolean hasNewNotifications) {
        this.hasNewNotifications = hasNewNotifications;
    }

    @Override
    public String toString() {
        return "UserNotificationInfoDTO{" +
                "userId=" + userId +
                ", areNotificationsMuted=" + areNotificationsMuted +
                ", lastReadNotifications=" + lastReadNotifications +
                ", hasNewNotifications=" + hasNewNotifications +
                '}';
    }
}
