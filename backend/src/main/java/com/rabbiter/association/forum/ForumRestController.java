package com.rabbiter.association.forum;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.forum.dto.CreateForumCommentRequest;
import com.rabbiter.association.forum.dto.CreateForumPostRequest;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ForumRestController {

    private final ForumFacade forumFacade;

    public ForumRestController(ForumFacade forumFacade) {
        this.forumFacade = forumFacade;
    }

    @GetMapping("/forum/posts")
    public R posts(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(forumFacade.list(authUser));
    }

    @GetMapping("/forum/feed")
    public R feed(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(forumFacade.feed(authUser));
    }

    @PostMapping("/forum/posts")
    public R create(@Validated @RequestBody CreateForumPostRequest request, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        forumFacade.create(authUser, request);
        return R.successMsg("帖子发布成功");
    }

    @GetMapping("/forum/posts/{postId}/comments")
    public R comments(@PathVariable String postId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(forumFacade.comments(authUser, postId));
    }

    @PostMapping("/forum/posts/{postId}/comments")
    public R createComment(@PathVariable String postId,
                           @Validated @RequestBody CreateForumCommentRequest request,
                           Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        forumFacade.createComment(authUser, postId, request);
        return R.successMsg("评论发布成功");
    }

    @PostMapping("/forum/posts/{postId}/likes")
    public R toggleLike(@PathVariable String postId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(forumFacade.toggleLike(authUser, postId));
    }
}
