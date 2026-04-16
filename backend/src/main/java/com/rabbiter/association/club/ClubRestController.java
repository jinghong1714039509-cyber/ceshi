package com.rabbiter.association.club;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ClubRestController {

    private final ClubApplicationFacade clubApplicationFacade;

    public ClubRestController(ClubApplicationFacade clubApplicationFacade) {
        this.clubApplicationFacade = clubApplicationFacade;
    }

    @GetMapping("/clubs")
    public R listClubs(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(clubApplicationFacade.listClubs(authUser));
    }

    @GetMapping("/clubs/{clubId}")
    public R club(@PathVariable String clubId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(clubApplicationFacade.getClubDetail(authUser, clubId));
    }

    @PostMapping("/clubs/{clubId}/join")
    public R joinClub(@PathVariable String clubId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        clubApplicationFacade.submitJoinApplication(authUser, clubId);
        return R.successMsg("申请已提交");
    }

    @GetMapping("/join-applications/mine")
    public R myApplications(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(clubApplicationFacade.listMine(authUser));
    }

    @GetMapping("/admin/join-applications")
    public R managedApplications(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(clubApplicationFacade.listManaged(authUser));
    }

    @PostMapping("/admin/join-applications/{applicationId}/approve")
    public R approve(@PathVariable String applicationId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        clubApplicationFacade.approve(authUser, applicationId);
        return R.successMsg("申请已通过");
    }

    @DeleteMapping("/join-applications/{applicationId}")
    public R withdraw(@PathVariable String applicationId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        clubApplicationFacade.withdraw(authUser, applicationId);
        return R.successMsg("申请已撤销");
    }
}
