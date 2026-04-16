package com.rabbiter.association.message.dto;

import javax.validation.constraints.NotBlank;

public class SendPrivateMessageRequest {
    @NotBlank(message = "私信内容不能为空")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
