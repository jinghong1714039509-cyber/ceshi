package com.rabbiter.association.activity.dto;

public class ActivitySignupResponse {

    private final String id;
    private final String createTime;
    private final String activeId;
    private final String userId;
    private final String userName;
    private final String userGender;
    private final String userPhone;

    public ActivitySignupResponse(String id, String createTime, String activeId, String userId,
                                  String userName, String userGender, String userPhone) {
        this.id = id;
        this.createTime = createTime;
        this.activeId = activeId;
        this.userId = userId;
        this.userName = userName;
        this.userGender = userGender;
        this.userPhone = userPhone;
    }

    public String getId() { return id; }
    public String getCreateTime() { return createTime; }
    public String getActiveId() { return activeId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserGender() { return userGender; }
    public String getUserPhone() { return userPhone; }
}
