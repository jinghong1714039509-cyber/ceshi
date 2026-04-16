package com.rabbiter.association.auth.dto;

public class LoginResponse {

    private final String token;
    private final String userId;
    private final String userName;
    private final String displayName;
    private final String role;
    private final String avatarUrl;

    public LoginResponse(String token, String userId, String userName, String displayName, String role, String avatarUrl) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
