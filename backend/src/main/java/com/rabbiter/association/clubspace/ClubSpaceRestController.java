package com.rabbiter.association.clubspace;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.clubspace.dto.CreateClubMessageRequest;
import com.rabbiter.association.clubspace.dto.CreateClubPostRequest;
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
public class ClubSpaceRestController {

    private final ClubSpaceFacade clubSpaceFacade;

    public ClubSpaceRestController(ClubSpaceFacade clubSpaceFacade) {
        this.clubSpaceFacade = clubSpaceFacade;
    }

    @GetMapping("/clubs/{clubId}/space")
    public R getSpace(@PathVariable String clubId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(clubSpaceFacade.getSpace(authUser, clubId));
    }

    @PostMapping("/clubs/{clubId}/space/posts")
    public R createPost(@PathVariable String clubId,
                        @Validated @RequestBody CreateClubPostRequest request,
                        Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        clubSpaceFacade.createPost(authUser, clubId, request);
        return R.successMsg("帖子发布成功");
    }

    @PostMapping("/clubs/{clubId}/space/messages")
    public R createMessage(@PathVariable String clubId,
                           @Validated @RequestBody CreateClubMessageRequest request,
                           Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        clubSpaceFacade.createMessage(authUser, clubId, request);
        return R.successMsg("消息发送成功");
    }
}
