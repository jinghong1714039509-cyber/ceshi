package com.rabbiter.association.auth;

public class AuthUser {

    private final String userId;
    private final String userName;
    private final String displayName;
    private final Integer type;
    private final String role;

    public AuthUser(String userId, String userName, String displayName, Integer type, String role) {
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.type = type;
        this.role = role;
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

    public Integer getType() {
        return type;
    }

    public String getRole() {
        return role;
    }
}
