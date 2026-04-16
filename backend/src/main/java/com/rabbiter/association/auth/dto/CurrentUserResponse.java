package com.rabbiter.association.auth.dto;

public class CurrentUserResponse {

    private final String userId;
    private final String userName;
    private final String displayName;
    private final Integer type;
    private final String role;
    private final String avatarUrl;

    public CurrentUserResponse(String userId, String userName, String displayName, Integer type, String role, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.type = type;
        this.role = role;
        this.avatarUrl = avatarUrl;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
