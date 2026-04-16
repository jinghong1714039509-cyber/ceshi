package com.rabbiter.association.auth;

import com.rabbiter.association.auth.dto.CurrentUserResponse;
import com.rabbiter.association.auth.dto.LoginRequest;
import com.rabbiter.association.auth.dto.LoginResponse;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.service.UsersService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsersService usersService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Users user = usersService.getUserByUserName(request.getUserName());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (!matches(user, request.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        upgradePasswordIfNeeded(user, request.getPassword());

        AuthRole role = AuthRole.fromType(user.getType());
        String displayName = StringUtils.hasText(user.getName()) ? user.getName() : user.getUserName();
        AuthUser authUser = new AuthUser(user.getId(), user.getUserName(), displayName, user.getType(), role.getCode());
        String token = jwtTokenProvider.generateToken(authUser);

        return new LoginResponse(
                token,
                authUser.getUserId(),
                authUser.getUserName(),
                authUser.getDisplayName(),
                authUser.getRole(),
                user.getAvatarUrl()
        );
    }

    public CurrentUserResponse currentUser(AuthUser authUser) {
        Users user = usersService.getOne(authUser.getUserId());
        return new CurrentUserResponse(
                authUser.getUserId(),
                authUser.getUserName(),
                authUser.getDisplayName(),
                authUser.getType(),
                authUser.getRole(),
                user == null ? "" : user.getAvatarUrl()
        );
    }

    public CurrentUserResponse updateAvatar(AuthUser authUser, String avatarUrl) {
        Users user = usersService.getOne(authUser.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setAvatarUrl(avatarUrl);
        usersService.update(user);
        return new CurrentUserResponse(
                authUser.getUserId(),
                authUser.getUserName(),
                authUser.getDisplayName(),
                authUser.getType(),
                authUser.getRole(),
                avatarUrl
        );
    }

    public AuthUser loadAuthUserById(String userId) {
        Users user = usersService.getOne(userId);
        if (user == null) {
            return null;
        }
        AuthRole role = AuthRole.fromType(user.getType());
        String displayName = StringUtils.hasText(user.getName()) ? user.getName() : user.getUserName();
        return new AuthUser(user.getId(), user.getUserName(), displayName, user.getType(), role.getCode());
    }

    private boolean matches(Users user, String rawPassword) {
        String storedPassword = user.getPassWord();
        if (!StringUtils.hasText(storedPassword)) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private void upgradePasswordIfNeeded(Users user, String rawPassword) {
        String storedPassword = user.getPassWord();
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            return;
        }
        user.setPassWord(passwordEncoder.encode(rawPassword));
        usersService.update(user);
    }
}
