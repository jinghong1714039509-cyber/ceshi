package com.rabbiter.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "activities")
public class Activities implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "comm")
    private String comm;

    @TableField(value = "detail")
    private String detail;

    @TableField(value = "ask")
    private String ask;

    @TableField(value = "total")
    private Integer total;

    @TableField(value = "active_time")
    private String activeTime;

    @TableField(value = "location")
    private String location;

    @TableField(value = "capacity")
    private Integer capacity;

    @TableField(value = "cover_image")
    private String coverImage;

    @TableField(value = "completion_status")
    private Integer completionStatus;

    @TableField(value = "completion_summary")
    private String completionSummary;

    @TableField(value = "completion_images")
    private String completionImages;

    @TableField(value = "completion_submitted_by")
    private String completionSubmittedBy;

    @TableField(value = "completion_submitted_at")
    private String completionSubmittedAt;

    @TableField(value = "completion_review_comment")
    private String completionReviewComment;

    @TableField(value = "completion_reviewed_by")
    private String completionReviewedBy;

    @TableField(value = "completion_reviewed_at")
    private String completionReviewedAt;

    @TableField(value = "team_id")
    private String teamId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getComm() { return comm; }
    public void setComm(String comm) { this.comm = comm; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getAsk() { return ask; }
    public void setAsk(String ask) { this.ask = ask; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public String getActiveTime() { return activeTime; }
    public void setActiveTime(String activeTime) { this.activeTime = activeTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(Integer completionStatus) { this.completionStatus = completionStatus; }
    public String getCompletionSummary() { return completionSummary; }
    public void setCompletionSummary(String completionSummary) { this.completionSummary = completionSummary; }
    public String getCompletionImages() { return completionImages; }
    public void setCompletionImages(String completionImages) { this.completionImages = completionImages; }
    public String getCompletionSubmittedBy() { return completionSubmittedBy; }
    public void setCompletionSubmittedBy(String completionSubmittedBy) { this.completionSubmittedBy = completionSubmittedBy; }
    public String getCompletionSubmittedAt() { return completionSubmittedAt; }
    public void setCompletionSubmittedAt(String completionSubmittedAt) { this.completionSubmittedAt = completionSubmittedAt; }
    public String getCompletionReviewComment() { return completionReviewComment; }
    public void setCompletionReviewComment(String completionReviewComment) { this.completionReviewComment = completionReviewComment; }
    public String getCompletionReviewedBy() { return completionReviewedBy; }
    public void setCompletionReviewedBy(String completionReviewedBy) { this.completionReviewedBy = completionReviewedBy; }
    public String getCompletionReviewedAt() { return completionReviewedAt; }
    public void setCompletionReviewedAt(String completionReviewedAt) { this.completionReviewedAt = completionReviewedAt; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
}
