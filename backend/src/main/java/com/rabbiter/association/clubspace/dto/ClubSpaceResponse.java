package com.rabbiter.association.clubspace.dto;

import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.notice.dto.NoticeResponse;

import java.util.List;

public class ClubSpaceResponse {
    private final String clubId;
    private final String clubName;
    private final String category;
    private final String managerName;
    private final int memberCount;
    private final boolean canManage;
    private final List<ActivitySummaryResponse> activities;
    private final List<NoticeResponse> notices;
    private final List<ClubPostResponse> posts;
    private final List<ClubMessageResponse> messages;

    public ClubSpaceResponse(String clubId,
                             String clubName,
                             String category,
                             String managerName,
                             int memberCount,
                             boolean canManage,
                             List<ActivitySummaryResponse> activities,
                             List<NoticeResponse> notices,
                             List<ClubPostResponse> posts,
                             List<ClubMessageResponse> messages) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.category = category;
        this.managerName = managerName;
        this.memberCount = memberCount;
        this.canManage = canManage;
        this.activities = activities;
        this.notices = notices;
        this.posts = posts;
        this.messages = messages;
    }

    public String getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public String getCategory() { return category; }
    public String getManagerName() { return managerName; }
    public int getMemberCount() { return memberCount; }
    public boolean isCanManage() { return canManage; }
    public List<ActivitySummaryResponse> getActivities() { return activities; }
    public List<NoticeResponse> getNotices() { return notices; }
    public List<ClubPostResponse> getPosts() { return posts; }
    public List<ClubMessageResponse> getMessages() { return messages; }
}
