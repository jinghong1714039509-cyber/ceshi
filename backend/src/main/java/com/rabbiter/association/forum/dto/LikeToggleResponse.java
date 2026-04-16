package com.rabbiter.association.forum.dto;

public class LikeToggleResponse {

    private final boolean liked;
    private final int likeCount;

    public LikeToggleResponse(boolean liked, int likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public boolean isLiked() { return liked; }
    public int getLikeCount() { return likeCount; }
}
