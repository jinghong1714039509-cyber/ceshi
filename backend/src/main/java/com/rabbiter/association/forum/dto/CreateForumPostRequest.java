package com.rabbiter.association.forum.dto;

import javax.validation.constraints.NotBlank;

public class CreateForumPostRequest {

    private String title;

    @NotBlank(message = "帖子内容不能为空")
    private String content;

    private String coverImage;

    private String topicTag;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getTopicTag() { return topicTag; }
    public void setTopicTag(String topicTag) { this.topicTag = topicTag; }
}
