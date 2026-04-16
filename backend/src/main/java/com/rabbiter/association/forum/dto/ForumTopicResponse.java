package com.rabbiter.association.forum.dto;

public class ForumTopicResponse {

    private final String topicTag;
    private final int postCount;

    public ForumTopicResponse(String topicTag, int postCount) {
        this.topicTag = topicTag;
        this.postCount = postCount;
    }

    public String getTopicTag() { return topicTag; }
    public int getPostCount() { return postCount; }
}
