package com.rabbiter.association.member;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberRestController {

    private final MemberFacade memberFacade;

    public MemberRestController(MemberFacade memberFacade) {
        this.memberFacade = memberFacade;
    }

    @GetMapping
    public R list(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(memberFacade.list(authUser));
    }

    @DeleteMapping("/{memberId}")
    public R remove(@PathVariable String memberId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        memberFacade.remove(authUser, memberId);
        return R.successMsg("成员已移除");
    }
}
