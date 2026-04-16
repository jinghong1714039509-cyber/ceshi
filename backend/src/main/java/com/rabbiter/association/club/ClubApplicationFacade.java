package com.rabbiter.association.club;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.club.dto.ClubDetailResponse;
import com.rabbiter.association.club.dto.ClubSummaryResponse;
import com.rabbiter.association.club.dto.JoinApplicationResponse;
import com.rabbiter.association.dao.ActivitiesDao;
import com.rabbiter.association.dao.ApplyLogsDao;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.dao.TeamTypesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.dao.UsersDao;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.ApplyLogs;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.TeamTypes;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.notice.dto.NoticeResponse;
import com.rabbiter.association.service.ActiveLogsService;
import com.rabbiter.association.service.ApplyLogsService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClubApplicationFacade {

    private final TeamsDao teamsDao;
    private final TeamTypesDao teamTypesDao;
    private final UsersDao usersDao;
    private final MembersDao membersDao;
    private final ApplyLogsDao applyLogsDao;
    private final ApplyLogsService applyLogsService;
    private final ActiveLogsService activeLogsService;
    private final ClubPermissionService clubPermissionService;
    private final ActivitiesDao activitiesDao;
    private final NoticesDao noticesDao;

    public ClubApplicationFacade(TeamsDao teamsDao,
                                 TeamTypesDao teamTypesDao,
                                 UsersDao usersDao,
                                 MembersDao membersDao,
                                 ApplyLogsDao applyLogsDao,
                                 ApplyLogsService applyLogsService,
                                 ActiveLogsService activeLogsService,
                                 ClubPermissionService clubPermissionService,
                                 ActivitiesDao activitiesDao,
                                 NoticesDao noticesDao) {
        this.teamsDao = teamsDao;
        this.teamTypesDao = teamTypesDao;
        this.usersDao = usersDao;
        this.membersDao = membersDao;
        this.applyLogsDao = applyLogsDao;
        this.applyLogsService = applyLogsService;
        this.activeLogsService = activeLogsService;
        this.clubPermissionService = clubPermissionService;
        this.activitiesDao = activitiesDao;
        this.noticesDao = noticesDao;
    }

    @Transactional(readOnly = true)
    public List<ClubSummaryResponse> listClubs(AuthUser authUser) {
        List<Teams> clubs = teamsDao.selectList(new QueryWrapper<Teams>().orderByDesc("create_time"));
        List<ClubSummaryResponse> responses = new ArrayList<>();
        for (Teams club : clubs) {
            Users manager = usersDao.selectById(club.getManager());
            TeamTypes type = teamTypesDao.selectById(club.getTypeId());
            boolean joined = isJoined(authUser.getUserId(), club.getId());
            boolean pendingApproval = hasPendingApplication(authUser.getUserId(), club.getId());
            boolean canManage = clubPermissionService.isClubManager(authUser, club.getId());
            boolean canEnterSpace = clubPermissionService.isMember(authUser, club.getId());
            responses.add(new ClubSummaryResponse(
                    club.getId(),
                    club.getName(),
                    type == null ? "未分类" : type.getName(),
                    club.getManager(),
                    manager == null ? "未知管理者" : displayName(manager),
                    club.getTotal(),
                    club.getCreateTime(),
                    joined,
                    pendingApproval,
                    canManage,
                    canEnterSpace
            ));
        }
        return responses;
    }

    @Transactional
    public void submitJoinApplication(AuthUser authUser, String clubId) {
        Teams club = teamsDao.selectById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("社团不存在");
        }
        if (AuthRole.fromType(authUser.getType()) != AuthRole.STUDENT) {
            throw new IllegalArgumentException("只有学生可以提交入团申请");
        }
        if (isJoined(authUser.getUserId(), clubId)) {
            throw new IllegalArgumentException("你已经是该社团成员");
        }
        if (!applyLogsService.isApply(authUser.getUserId(), clubId)) {
            throw new IllegalArgumentException("你已经提交过申请，请等待审批");
        }

        ApplyLogs applyLog = new ApplyLogs();
        applyLog.setId(IDUtils.makeIDByCurrent());
        applyLog.setStatus(0);
        applyLog.setCreateTime(DateUtils.getNowDate());
        applyLog.setTeamId(clubId);
        applyLog.setUserId(authUser.getUserId());
        applyLogsService.add(applyLog);
    }

    @Transactional(readOnly = true)
    public List<JoinApplicationResponse> listMine(AuthUser authUser) {
        return mapApplications(applyLogsDao.qryPageInfo(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 100),
                authUser.getUserId(),
                null,
                null
        ).getRecords());
    }

    @Transactional(readOnly = true)
    public List<JoinApplicationResponse> listManaged(AuthUser authUser) {
        AuthRole role = AuthRole.fromType(authUser.getType());
        if (role == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色无权查看审批列表");
        }
        return mapApplications(applyLogsDao.qryManPageInfo(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 100),
                authUser.getUserId(),
                null,
                null
        ).getRecords().stream()
                .filter(item -> clubPermissionService.isClubManager(authUser, valueAsString(item.get("teamId"))))
                .toList());
    }

    @Transactional
    public void approve(AuthUser authUser, String applicationId) {
        ApplyLogs applyLog = applyLogsService.getOne(applicationId);
        if (applyLog == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }
        Teams club = teamsDao.selectById(applyLog.getTeamId());
        if (club == null) {
            throw new IllegalArgumentException("社团不存在");
        }
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色无权审批");
        }
        clubPermissionService.ensureManager(authUser, club.getId());
        if (applyLog.getStatus() != null && applyLog.getStatus() == 1) {
            return;
        }
        applyLog.setStatus(1);
        applyLogsService.update(applyLog);
    }

    @Transactional
    public void withdraw(AuthUser authUser, String applicationId) {
        ApplyLogs applyLog = applyLogsService.getOne(applicationId);
        if (applyLog == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }
        if (!authUser.getUserId().equals(applyLog.getUserId())) {
            throw new IllegalArgumentException("你只能撤销自己的申请");
        }
        if (applyLog.getStatus() != null && applyLog.getStatus() != 0) {
            throw new IllegalArgumentException("只有待审批申请可以撤销");
        }
        applyLogsService.delete(applyLog);
    }

    @Transactional(readOnly = true)
    public ClubDetailResponse getClubDetail(AuthUser authUser, String clubId) {
        Teams club = teamsDao.selectById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("社团不存在");
        }
        Users manager = usersDao.selectById(club.getManager());
        TeamTypes type = teamTypesDao.selectById(club.getTypeId());
        boolean joined = isJoined(authUser.getUserId(), club.getId());
        boolean canManage = clubPermissionService.isClubManager(authUser, clubId);
        boolean canEnterSpace = clubPermissionService.isMember(authUser, clubId);

        List<ActivitySummaryResponse> activities = activitiesDao.selectList(new QueryWrapper<Activities>()
                        .eq("team_id", clubId)
                        .orderByDesc("active_time"))
                .stream()
                .limit(8)
                .map(activity -> new ActivitySummaryResponse(
                        activity.getId(),
                        activity.getName(),
                        activity.getComm(),
                        activity.getDetail(),
                        activity.getAsk(),
                        activity.getTotal(),
                        activity.getActiveTime(),
                        activity.getLocation(),
                        activity.getCapacity(),
                        activity.getCoverImage(),
                        clubId,
                        club.getName(),
                        !activeLogsService.isActive(activity.getId(), authUser.getUserId()),
                        canManage,
                        activity.getCompletionStatus() == null ? 0 : activity.getCompletionStatus(),
                        "进行中",
                        activity.getCompletionSummary(),
                        java.util.Collections.emptyList(),
                        activity.getCompletionSubmittedAt(),
                        activity.getCompletionReviewComment(),
                        activity.getCompletionReviewedAt(),
                        canManage,
                        false
                ))
                .toList();

        List<NoticeResponse> notices = noticesDao.selectList(new QueryWrapper<Notices>()
                        .eq("team_id", clubId)
                        .orderByDesc("create_time"))
                .stream()
                .limit(6)
                .map(notice -> new NoticeResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getDetail(),
                        notice.getCreateTime(),
                        notice.getTeamId(),
                        club.getName(),
                        false
                ))
                .toList();

        return new ClubDetailResponse(
                club.getId(),
                club.getName(),
                type == null ? "未分类" : type.getName(),
                manager == null ? "未设置" : displayName(manager),
                club.getTotal(),
                club.getCreateTime(),
                joined,
                canManage,
                canEnterSpace,
                activities,
                notices
        );
    }

    private List<JoinApplicationResponse> mapApplications(List<Map<String, Object>> records) {
        List<JoinApplicationResponse> responses = new ArrayList<>();
        for (Map<String, Object> record : records) {
            Integer status = valueAsInteger(record.get("status"));
            responses.add(new JoinApplicationResponse(
                    valueAsString(record.get("id")),
                    status,
                    statusLabel(status),
                    valueAsString(record.get("createTime")),
                    valueAsString(record.get("teamId")),
                    valueAsString(record.get("teamName")),
                    valueAsString(record.get("userId")),
                    valueAsString(record.get("userName")),
                    valueAsString(record.get("userPhone"))
            ));
        }
        return responses;
    }

    private boolean isJoined(String userId, String clubId) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        Long count = membersDao.selectCount(new QueryWrapper<com.rabbiter.association.entity.Members>()
                .eq("user_id", userId)
                .eq("team_id", clubId));
        return count != null && count > 0;
    }

    private boolean hasPendingApplication(String userId, String clubId) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        Long count = applyLogsDao.selectCount(new QueryWrapper<ApplyLogs>()
                .eq("user_id", userId)
                .eq("team_id", clubId)
                .eq("status", 0));
        return count != null && count > 0;
    }

    private String displayName(Users user) {
        return StringUtils.hasText(user.getName()) ? user.getName() : user.getUserName();
    }

    private String valueAsString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Integer valueAsInteger(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value == null) return null;
        return Integer.parseInt(String.valueOf(value));
    }

    private String statusLabel(Integer status) {
        if (status == null) return "未知";
        if (status == 1) return "已通过";
        if (status == 0) return "待审批";
        return "已拒绝";
    }
}
