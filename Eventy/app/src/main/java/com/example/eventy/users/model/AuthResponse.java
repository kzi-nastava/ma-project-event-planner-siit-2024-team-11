package com.example.eventy.users.model;

public class AuthResponse {

    private String accessToken;
    private Long expiresIn;
    private Long userId;

    public AuthResponse() {
        this.accessToken = null;
        this.expiresIn = null;
        this.userId = null;
    }

    public AuthResponse(String accessToken, long expiresIn, long userId) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
