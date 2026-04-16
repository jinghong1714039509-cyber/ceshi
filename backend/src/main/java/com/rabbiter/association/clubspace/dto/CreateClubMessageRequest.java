package com.rabbiter.association.clubspace.dto;

import javax.validation.constraints.NotBlank;

public class CreateClubMessageRequest {
    @NotBlank(message = "消息内容不能为空")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
