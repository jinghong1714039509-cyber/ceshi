package com.rabbiter.association.activity.dto;

import javax.validation.constraints.NotBlank;

public class CreateActivityRequest {

    @NotBlank(message = "社团不能为空")
    private String clubId;

    @NotBlank(message = "活动名称不能为空")
    private String name;

    @NotBlank(message = "活动简介不能为空")
    private String summary;

    @NotBlank(message = "活动详情不能为空")
    private String detail;

    @NotBlank(message = "活动要求不能为空")
    private String requirement;

    @NotBlank(message = "活动时间不能为空")
    private String activeTime;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    private Integer capacity;

    private String coverImage;

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getRequirement() { return requirement; }
    public void setRequirement(String requirement) { this.requirement = requirement; }
    public String getActiveTime() { return activeTime; }
    public void setActiveTime(String activeTime) { this.activeTime = activeTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}
