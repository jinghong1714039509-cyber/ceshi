package com.rabbiter.association.clubspace.dto;

import javax.validation.constraints.NotBlank;

public class CreateClubPostRequest {
    @NotBlank(message = "帖子标题不能为空")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
