package com.rabbiter.association.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubPermissionService;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.member.dto.MemberResponse;
import com.rabbiter.association.service.MembersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MemberFacade {

    private final MembersDao membersDao;
    private final MembersService membersService;
    private final ClubPermissionService clubPermissionService;

    public MemberFacade(MembersDao membersDao, MembersService membersService, ClubPermissionService clubPermissionService) {
        this.membersDao = membersDao;
        this.membersService = membersService;
        this.clubPermissionService = clubPermissionService;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> list(AuthUser authUser) {
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色无权查看成员列表");
        }

        List<Map<String, Object>> records = clubPermissionService.isSuperAdmin(authUser)
                ? membersDao.qryPageAll(new Page<>(1, 300), null, null).getRecords()
                : membersDao.qryPageByManId(new Page<>(1, 300), authUser.getUserId(), null, null).getRecords();

        List<MemberResponse> responses = new ArrayList<>();
        for (Map<String, Object> record : records) {
            String teamId = stringValue(record.get("teamId"));
            String userId = stringValue(record.get("userId"));
            responses.add(new MemberResponse(
                    stringValue(record.get("id")),
                    stringValue(record.get("createTime")),
                    teamId,
                    stringValue(record.get("teamName")),
                    userId,
                    stringValue(record.get("userName")),
                    stringValue(record.get("userGender")),
                    intValue(record.get("userAge")),
                    stringValue(record.get("userPhone")),
                    membersService.isManager(teamId, userId)
            ));
        }
        return responses;
    }

    @Transactional
    public void remove(AuthUser authUser, String memberId) {
        Members member = membersService.getOne(memberId);
        if (member == null) {
            throw new IllegalArgumentException("成员记录不存在");
        }
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色无权移除成员");
        }
        if (membersService.isManager(member.getTeamId(), member.getUserId())) {
            throw new IllegalArgumentException("社团管理者不可被移除");
        }
        clubPermissionService.ensureManager(authUser, member.getTeamId());
        membersService.delete(member);
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
}
