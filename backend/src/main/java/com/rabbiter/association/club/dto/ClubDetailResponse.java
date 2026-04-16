package com.rabbiter.association.club.dto;

import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.notice.dto.NoticeResponse;

import java.util.List;

public class ClubDetailResponse {

    private final String id;
    private final String name;
    private final String category;
    private final String managerName;
    private final Integer totalMembers;
    private final String createdAt;
    private final boolean joined;
    private final boolean canManage;
    private final boolean canEnterSpace;
    private final List<ActivitySummaryResponse> activities;
    private final List<NoticeResponse> notices;

    public ClubDetailResponse(String id, String name, String category, String managerName,
                              Integer totalMembers, String createdAt, boolean joined,
                              boolean canManage, boolean canEnterSpace,
                              List<ActivitySummaryResponse> activities, List<NoticeResponse> notices) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.managerName = managerName;
        this.totalMembers = totalMembers;
        this.createdAt = createdAt;
        this.joined = joined;
        this.canManage = canManage;
        this.canEnterSpace = canEnterSpace;
        this.activities = activities;
        this.notices = notices;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getManagerName() { return managerName; }
    public Integer getTotalMembers() { return totalMembers; }
    public String getCreatedAt() { return createdAt; }
    public boolean isJoined() { return joined; }
    public boolean isCanManage() { return canManage; }
    public boolean isCanEnterSpace() { return canEnterSpace; }
    public List<ActivitySummaryResponse> getActivities() { return activities; }
    public List<NoticeResponse> getNotices() { return notices; }
}
