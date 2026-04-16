package com.rabbiter.association.auth.dto;

import javax.validation.constraints.NotBlank;

public class UpdateAvatarRequest {

    @NotBlank(message = "头像地址不能为空")
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
