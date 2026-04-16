package com.rabbiter.association.clubspace.dto;

public class ClubPostResponse {
    private final String id;
    private final String title;
    private final String content;
    private final boolean pinned;
    private final String createTime;
    private final String authorName;

    public ClubPostResponse(String id, String title, String content, boolean pinned, String createTime, String authorName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.createTime = createTime;
        this.authorName = authorName;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isPinned() { return pinned; }
    public String getCreateTime() { return createTime; }
    public String getAuthorName() { return authorName; }
}
