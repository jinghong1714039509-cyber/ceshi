package com.rabbiter.association.forum.dto;

public class ForumCommentResponse {

    private final String id;
    private final String content;
    private final String createTime;
    private final String authorId;
    private final String authorName;
    private final String authorAvatar;
    private final String authorRole;
    private final boolean mine;

    public ForumCommentResponse(String id,
                                String content,
                                String createTime,
                                String authorId,
                                String authorName,
                                String authorAvatar,
                                String authorRole,
                                boolean mine) {
        this.id = id;
        this.content = content;
        this.createTime = createTime;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.authorRole = authorRole;
        this.mine = mine;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getCreateTime() { return createTime; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public String getAuthorRole() { return authorRole; }
    public boolean isMine() { return mine; }
}
