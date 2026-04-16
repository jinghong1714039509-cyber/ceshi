package com.rabbiter.association.forum.dto;

import java.util.List;

public class ForumFeedResponse {

    private final List<ForumPostResponse> posts;
    private final List<ForumTopicResponse> hotTopics;

    public ForumFeedResponse(List<ForumPostResponse> posts, List<ForumTopicResponse> hotTopics) {
        this.posts = posts;
        this.hotTopics = hotTopics;
    }

    public List<ForumPostResponse> getPosts() { return posts; }
    public List<ForumTopicResponse> getHotTopics() { return hotTopics; }
}
