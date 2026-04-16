package com.rabbiter.association.notice;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.notice.dto.CreateNoticeRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
public class NoticeRestController {

    private final NoticeFacade noticeFacade;

    public NoticeRestController(NoticeFacade noticeFacade) {
        this.noticeFacade = noticeFacade;
    }

    @GetMapping
    public R list(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(noticeFacade.list(authUser));
    }

    @PostMapping
    public R create(@Validated @RequestBody CreateNoticeRequest request, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        noticeFacade.create(authUser, request);
        return R.successMsg("公告已发布");
    }
}
