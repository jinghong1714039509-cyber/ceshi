package com.rabbiter.association.activity.dto;

import javax.validation.constraints.NotBlank;

public class ReviewActivityCompletionRequest {
    private boolean approved;

    @NotBlank(message = "请填写指导意见")
    private String comment;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
