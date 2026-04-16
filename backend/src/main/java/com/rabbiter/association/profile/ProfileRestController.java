package com.rabbiter.association.profile;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileRestController {

    private final ProfileFacade profileFacade;

    public ProfileRestController(ProfileFacade profileFacade) {
        this.profileFacade = profileFacade;
    }

    @GetMapping("/overview")
    public R overview(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(profileFacade.overview(authUser));
    }
}
