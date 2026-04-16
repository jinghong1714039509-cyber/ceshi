package com.rabbiter.association.club;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.Teams;
import org.springframework.stereotype.Service;

@Service
public class ClubPermissionService {

    private final TeamsDao teamsDao;
    private final MembersDao membersDao;

    public ClubPermissionService(TeamsDao teamsDao, MembersDao membersDao) {
        this.teamsDao = teamsDao;
        this.membersDao = membersDao;
    }

    public boolean isSuperAdmin(AuthUser authUser) {
        return AuthRole.fromType(authUser.getType()) == AuthRole.SUPER_ADMIN;
    }

    public boolean isClubManager(AuthUser authUser, String teamId) {
        if (isSuperAdmin(authUser)) {
            return true;
        }
        Teams team = teamsDao.selectById(teamId);
        return team != null && authUser.getUserId().equals(team.getManager());
    }

    public boolean isMember(AuthUser authUser, String teamId) {
        if (isClubManager(authUser, teamId)) {
            return true;
        }
        Long count = membersDao.selectCount(new QueryWrapper<Members>()
                .eq("team_id", teamId)
                .eq("user_id", authUser.getUserId()));
        return count != null && count > 0;
    }

    public void ensureManager(AuthUser authUser, String teamId) {
        if (!isClubManager(authUser, teamId)) {
            throw new IllegalArgumentException("当前账号无权管理该社团");
        }
    }

    public void ensureMember(AuthUser authUser, String teamId) {
        if (!isMember(authUser, teamId)) {
            throw new IllegalArgumentException("仅社团成员可进入该社团空间");
        }
    }
}
