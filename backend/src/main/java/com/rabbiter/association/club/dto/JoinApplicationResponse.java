package com.rabbiter.association.club.dto;

public class JoinApplicationResponse {

    private final String id;
    private final Integer status;
    private final String statusLabel;
    private final String createdAt;
    private final String clubId;
    private final String clubName;
    private final String applicantId;
    private final String applicantName;
    private final String applicantPhone;

    public JoinApplicationResponse(String id, Integer status, String statusLabel, String createdAt, String clubId,
                                   String clubName, String applicantId, String applicantName, String applicantPhone) {
        this.id = id;
        this.status = status;
        this.statusLabel = statusLabel;
        this.createdAt = createdAt;
        this.clubId = clubId;
        this.clubName = clubName;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
    }

    public String getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getClubId() {
        return clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }
}
