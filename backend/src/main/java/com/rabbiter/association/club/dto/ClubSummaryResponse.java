package com.rabbiter.association.club.dto;

public class ClubSummaryResponse {

    private final String id;
    private final String name;
    private final String category;
    private final String managerId;
    private final String managerName;
    private final Integer totalMembers;
    private final String createdAt;
    private final boolean joined;
    private final boolean pendingApproval;
    private final boolean canManage;
    private final boolean canEnterSpace;

    public ClubSummaryResponse(String id,
                               String name,
                               String category,
                               String managerId,
                               String managerName,
                               Integer totalMembers,
                               String createdAt,
                               boolean joined,
                               boolean pendingApproval,
                               boolean canManage,
                               boolean canEnterSpace) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.managerId = managerId;
        this.managerName = managerName;
        this.totalMembers = totalMembers;
        this.createdAt = createdAt;
        this.joined = joined;
        this.pendingApproval = pendingApproval;
        this.canManage = canManage;
        this.canEnterSpace = canEnterSpace;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getManagerId() { return managerId; }
    public String getManagerName() { return managerName; }
    public Integer getTotalMembers() { return totalMembers; }
    public String getCreatedAt() { return createdAt; }
    public boolean isJoined() { return joined; }
    public boolean isPendingApproval() { return pendingApproval; }
    public boolean isCanManage() { return canManage; }
    public boolean isCanEnterSpace() { return canEnterSpace; }
}
