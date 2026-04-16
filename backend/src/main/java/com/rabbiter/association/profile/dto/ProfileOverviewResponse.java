package com.rabbiter.association.profile.dto;

import java.util.List;

public class ProfileOverviewResponse {

    private final String userId;
    private final String userName;
    private final String displayName;
    private final String role;
    private final List<?> myClubs;
    private final List<?> myActivities;
    private final List<?> myApplications;

    public ProfileOverviewResponse(String userId, String userName, String displayName, String role,
                                   List<?> myClubs, List<?> myActivities, List<?> myApplications) {
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.role = role;
        this.myClubs = myClubs;
        this.myActivities = myActivities;
        this.myApplications = myApplications;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public List<?> getMyClubs() { return myClubs; }
    public List<?> getMyActivities() { return myActivities; }
    public List<?> getMyApplications() { return myApplications; }
}
