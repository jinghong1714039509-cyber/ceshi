package com.rabbiter.association.user;

import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.user.dto.AdminCreateUserRequest;
import com.rabbiter.association.user.dto.RegisterRequest;
import com.rabbiter.association.user.dto.UserResponse;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserFacade {

    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;

    public UserFacade(UsersService usersService, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (usersService.getUserByUserName(request.getUserName()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        Users user = new Users();
        user.setId(IDUtils.makeIDByCurrent());
        user.setUserName(request.getUserName());
        user.setPassWord(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(1);
        user.setType(2);
        user.setCreateTime(DateUtils.getNowDate());
        usersService.add(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(AuthUser authUser) {
        if (AuthRole.fromType(authUser.getType()) != AuthRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("当前角色无权查看用户列表");
        }
        Users condition = new Users();
        List<Map<String, Object>> rows = usersService.getPageInfo(1L, 200L, condition).getData();
        List<UserResponse> responses = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer status = intValue(row.get("status"));
            Integer type = intValue(row.get("type"));
            responses.add(new UserResponse(
                    stringValue(row.get("id")),
                    stringValue(row.get("userName")),
                    stringValue(row.get("name")),
                    stringValue(row.get("gender")),
                    intValue(row.get("age")),
                    stringValue(row.get("phone")),
                    stringValue(row.get("address")),
                    status,
                    statusLabel(status),
                    type,
                    roleLabel(type),
                    stringValue(row.get("createTime"))
            ));
        }
        return responses;
    }

    @Transactional
    public void adminCreate(AuthUser authUser, AdminCreateUserRequest request) {
        if (AuthRole.fromType(authUser.getType()) != AuthRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("当前角色无权创建用户");
        }
        if (usersService.getUserByUserName(request.getUserName()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        Users user = new Users();
        user.setId(IDUtils.makeIDByCurrent());
        user.setUserName(request.getUserName());
        user.setPassWord(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(request.getStatus());
        user.setType(request.getType());
        user.setCreateTime(DateUtils.getNowDate());
        usersService.add(user);
    }

    @Transactional
    public void updateStatus(AuthUser authUser, String userId, Integer status) {
        if (AuthRole.fromType(authUser.getType()) != AuthRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("当前角色无权修改用户状态");
        }
        Users user = usersService.getOne(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(status);
        usersService.update(user);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Integer intValue(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value == null) return null;
        return Integer.parseInt(String.valueOf(value));
    }

    private String statusLabel(Integer status) {
        return status != null && status == 1 ? "启用" : "停用";
    }

    private String roleLabel(Integer type) {
        if (type == null) return "未知";
        if (type == 0) return "系统管理员";
        if (type == 1) return "社团管理员";
        if (type == 3) return "教师管理员";
        return "学生";
    }
}
