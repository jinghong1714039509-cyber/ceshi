package com.rabbiter.association.activity.dto;

import java.util.List;

public class ActivitySummaryResponse {

    private final String id;
    private final String name;
    private final String summary;
    private final String detail;
    private final String requirement;
    private final Integer total;
    private final String activeTime;
    private final String location;
    private final Integer capacity;
    private final String coverImage;
    private final String clubId;
    private final String clubName;
    private final boolean joined;
    private final boolean manageable;
    private final Integer completionStatus;
    private final String completionStatusLabel;
    private final String completionSummary;
    private final List<String> completionImages;
    private final String completionSubmittedAt;
    private final String completionReviewComment;
    private final String completionReviewedAt;
    private final boolean canSubmitCompletion;
    private final boolean canReviewCompletion;

    public ActivitySummaryResponse(String id, String name, String summary, String detail, String requirement,
                                   Integer total, String activeTime, String location, Integer capacity,
                                   String coverImage, String clubId, String clubName,
                                   boolean joined, boolean manageable,
                                   Integer completionStatus, String completionStatusLabel, String completionSummary,
                                   List<String> completionImages, String completionSubmittedAt,
                                   String completionReviewComment, String completionReviewedAt,
                                   boolean canSubmitCompletion, boolean canReviewCompletion) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.detail = detail;
        this.requirement = requirement;
        this.total = total;
        this.activeTime = activeTime;
        this.location = location;
        this.capacity = capacity;
        this.coverImage = coverImage;
        this.clubId = clubId;
        this.clubName = clubName;
        this.joined = joined;
        this.manageable = manageable;
        this.completionStatus = completionStatus;
        this.completionStatusLabel = completionStatusLabel;
        this.completionSummary = completionSummary;
        this.completionImages = completionImages;
        this.completionSubmittedAt = completionSubmittedAt;
        this.completionReviewComment = completionReviewComment;
        this.completionReviewedAt = completionReviewedAt;
        this.canSubmitCompletion = canSubmitCompletion;
        this.canReviewCompletion = canReviewCompletion;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSummary() { return summary; }
    public String getDetail() { return detail; }
    public String getRequirement() { return requirement; }
    public Integer getTotal() { return total; }
    public String getActiveTime() { return activeTime; }
    public String getLocation() { return location; }
    public Integer getCapacity() { return capacity; }
    public String getCoverImage() { return coverImage; }
    public String getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public boolean isJoined() { return joined; }
    public boolean isManageable() { return manageable; }
    public Integer getCompletionStatus() { return completionStatus; }
    public String getCompletionStatusLabel() { return completionStatusLabel; }
    public String getCompletionSummary() { return completionSummary; }
    public List<String> getCompletionImages() { return completionImages; }
    public String getCompletionSubmittedAt() { return completionSubmittedAt; }
    public String getCompletionReviewComment() { return completionReviewComment; }
    public String getCompletionReviewedAt() { return completionReviewedAt; }
    public boolean isCanSubmitCompletion() { return canSubmitCompletion; }
    public boolean isCanReviewCompletion() { return canReviewCompletion; }
}
