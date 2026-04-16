package com.rabbiter.association.auth;

import com.rabbiter.association.auth.dto.LoginRequest;
import com.rabbiter.association.auth.dto.UpdateAvatarRequest;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R login(@Validated @RequestBody LoginRequest request) {
        return R.success("登录成功", authService.login(request));
    }

    @GetMapping("/me")
    public R me(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(authService.currentUser(authUser));
    }

    @PostMapping("/avatar")
    public R updateAvatar(@Validated @RequestBody UpdateAvatarRequest request, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(authService.updateAvatar(authUser, request.getAvatarUrl()));
    }
}
