package com.rabbiter.association.forum.dto;

import java.util.List;

public class ForumPostResponse {

    private final String id;
    private final String title;
    private final String content;
    private final String coverImage;
    private final String createTime;
    private final String authorId;
    private final String authorName;
    private final String authorAvatar;
    private final String authorRole;
    private final String topicTag;
    private final boolean ownPost;
    private final boolean liked;
    private final int likeCount;
    private final int commentCount;
    private final List<ForumCommentResponse> recentComments;

    public ForumPostResponse(String id,
                             String title,
                             String content,
                             String coverImage,
                             String createTime,
                             String authorId,
                             String authorName,
                             String authorAvatar,
                             String authorRole,
                             String topicTag,
                             boolean ownPost,
                             boolean liked,
                             int likeCount,
                             int commentCount,
                             List<ForumCommentResponse> recentComments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.createTime = createTime;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.authorRole = authorRole;
        this.topicTag = topicTag;
        this.ownPost = ownPost;
        this.liked = liked;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.recentComments = recentComments;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCoverImage() { return coverImage; }
    public String getCreateTime() { return createTime; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public String getAuthorRole() { return authorRole; }
    public String getTopicTag() { return topicTag; }
    public boolean isOwnPost() { return ownPost; }
    public boolean isLiked() { return liked; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public List<ForumCommentResponse> getRecentComments() { return recentComments; }
}
