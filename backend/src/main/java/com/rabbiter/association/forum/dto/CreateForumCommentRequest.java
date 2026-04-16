package com.rabbiter.association.forum.dto;

import javax.validation.constraints.NotBlank;

public class CreateForumCommentRequest {

    @NotBlank(message = "评论内容不能为空")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
