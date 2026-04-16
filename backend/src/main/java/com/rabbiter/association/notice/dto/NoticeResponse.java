package com.rabbiter.association.notice.dto;

public class NoticeResponse {

    private final String id;
    private final String title;
    private final String detail;
    private final String createTime;
    private final String teamId;
    private final String teamName;
    private final boolean systemNotice;

    public NoticeResponse(String id, String title, String detail, String createTime,
                          String teamId, String teamName, boolean systemNotice) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.createTime = createTime;
        this.teamId = teamId;
        this.teamName = teamName;
        this.systemNotice = systemNotice;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public String getCreateTime() { return createTime; }
    public String getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public boolean isSystemNotice() { return systemNotice; }
}
