package com.rabbiter.association.member.dto;

public class MemberResponse {

    private final String id;
    private final String createTime;
    private final String teamId;
    private final String teamName;
    private final String userId;
    private final String userName;
    private final String userGender;
    private final Integer userAge;
    private final String userPhone;
    private final boolean manager;

    public MemberResponse(String id, String createTime, String teamId, String teamName, String userId,
                          String userName, String userGender, Integer userAge, String userPhone, boolean manager) {
        this.id = id;
        this.createTime = createTime;
        this.teamId = teamId;
        this.teamName = teamName;
        this.userId = userId;
        this.userName = userName;
        this.userGender = userGender;
        this.userAge = userAge;
        this.userPhone = userPhone;
        this.manager = manager;
    }

    public String getId() { return id; }
    public String getCreateTime() { return createTime; }
    public String getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserGender() { return userGender; }
    public Integer getUserAge() { return userAge; }
    public String getUserPhone() { return userPhone; }
    public boolean isManager() { return manager; }
}
