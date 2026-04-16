package com.rabbiter.association.user;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.user.dto.AdminCreateUserRequest;
import com.rabbiter.association.user.dto.RegisterRequest;
import com.rabbiter.association.user.dto.UpdateUserStatusRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserFacade userFacade;

    public UserRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/register")
    public R register(@Validated @RequestBody RegisterRequest request) {
        userFacade.register(request);
        return R.successMsg("注册成功，请返回登录");
    }

    @GetMapping
    public R list(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(userFacade.list(authUser));
    }

    @PostMapping
    public R create(@Validated @RequestBody AdminCreateUserRequest request, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        userFacade.adminCreate(authUser, request);
        return R.successMsg("用户已创建");
    }

    @PatchMapping("/{userId}/status")
    public R updateStatus(@PathVariable String userId,
                          @Validated @RequestBody UpdateUserStatusRequest request,
                          Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        userFacade.updateStatus(authUser, userId, request.getStatus());
        return R.successMsg("用户状态已更新");
    }
}
