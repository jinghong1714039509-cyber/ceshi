package com.rabbiter.association.clubspace.dto;

public class ClubMessageResponse {
    private final String id;
    private final String authorId;
    private final String content;
    private final String createTime;
    private final String authorName;

    public ClubMessageResponse(String id, String authorId, String content, String createTime, String authorName) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.createTime = createTime;
        this.authorName = authorName;
    }

    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public String getCreateTime() { return createTime; }
    public String getAuthorName() { return authorName; }
}
