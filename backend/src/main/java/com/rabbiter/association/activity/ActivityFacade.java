package com.rabbiter.association.activity;

import com.rabbiter.association.activity.dto.ActivitySignupResponse;
import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.activity.dto.CreateActivityRequest;
import com.rabbiter.association.activity.dto.ReviewActivityCompletionRequest;
import com.rabbiter.association.activity.dto.SubmitActivityCompletionRequest;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubPermissionService;
import com.rabbiter.association.dao.ActivitiesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.entity.ActiveLogs;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.service.ActiveLogsService;
import com.rabbiter.association.service.ActivitiesService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ActivityFacade {

    private final ActivitiesDao activitiesDao;
    private final TeamsDao teamsDao;
    private final ActivitiesService activitiesService;
    private final ActiveLogsService activeLogsService;
    private final ClubPermissionService clubPermissionService;

    public ActivityFacade(ActivitiesDao activitiesDao,
                          TeamsDao teamsDao,
                          ActivitiesService activitiesService,
                          ActiveLogsService activeLogsService,
                          ClubPermissionService clubPermissionService) {
        this.activitiesDao = activitiesDao;
        this.teamsDao = teamsDao;
        this.activitiesService = activitiesService;
        this.activeLogsService = activeLogsService;
        this.clubPermissionService = clubPermissionService;
    }

    @Transactional(readOnly = true)
    public List<ActivitySummaryResponse> listActivities(AuthUser authUser) {
        boolean publicView = "guest".equals(authUser.getRole());
        List<Map<String, Object>> records = activitiesDao
                .qryPageAll(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 200), null, null)
                .getRecords();

        List<ActivitySummaryResponse> responses = new ArrayList<>();
        for (Map<String, Object> record : records) {
            String activityId = valueAsString(record.get("id"));
            String clubId = valueAsString(record.get("teamId"));
            boolean joined = !publicView && !activeLogsService.isActive(activityId, authUser.getUserId());
            boolean manageable = !publicView && clubPermissionService.isClubManager(authUser, clubId);
            responses.add(toResponse(
                    activityId,
                    valueAsString(record.get("name")),
                    valueAsString(record.get("comm")),
                    valueAsString(record.get("detail")),
                    valueAsString(record.get("ask")),
                    valueAsInteger(record.get("total")),
                    valueAsString(record.get("activeTime")),
                    valueAsString(record.get("location")),
                    valueAsInteger(record.get("capacity")),
                    valueAsString(record.get("coverImage")),
                    clubId,
                    valueAsString(record.get("teamName")),
                    joined,
                    manageable,
                    valueAsInteger(record.get("completionStatus")),
                    valueAsString(record.get("completionSummary")),
                    valueAsString(record.get("completionImages")),
                    valueAsString(record.get("completionSubmittedAt")),
                    valueAsString(record.get("completionReviewComment")),
                    valueAsString(record.get("completionReviewedAt")),
                    authUser
            ));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public ActivitySummaryResponse getActivity(AuthUser authUser, String activityId) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        Teams club = teamsDao.selectById(activity.getTeamId());
        boolean joined = !"guest".equals(authUser.getRole()) && !activeLogsService.isActive(activityId, authUser.getUserId());
        boolean manageable = clubPermissionService.isClubManager(authUser, activity.getTeamId());
        return toResponse(
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
                activity.getTeamId(),
                club == null ? "" : club.getName(),
                joined,
                manageable,
                valueOrDefault(activity.getCompletionStatus(), 0),
                activity.getCompletionSummary(),
                activity.getCompletionImages(),
                activity.getCompletionSubmittedAt(),
                activity.getCompletionReviewComment(),
                activity.getCompletionReviewedAt(),
                authUser
        );
    }

    @Transactional
    public void createActivity(AuthUser authUser, CreateActivityRequest request) {
        Teams club = teamsDao.selectById(request.getClubId());
        if (club == null) {
            throw new IllegalArgumentException("社团不存在");
        }
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色不能发布活动");
        }
        clubPermissionService.ensureManager(authUser, request.getClubId());

        Activities activity = new Activities();
        activity.setId(IDUtils.makeIDByCurrent());
        activity.setName(request.getName());
        activity.setComm(request.getSummary());
        activity.setDetail(request.getDetail());
        activity.setAsk(request.getRequirement());
        activity.setTotal(0);
        activity.setActiveTime(request.getActiveTime());
        activity.setLocation(request.getLocation());
        activity.setCapacity(request.getCapacity() == null ? 0 : request.getCapacity());
        activity.setCoverImage(StringUtils.hasText(request.getCoverImage()) ? request.getCoverImage() : defaultCoverForClub(request.getClubId()));
        activity.setCompletionStatus(0);
        activity.setTeamId(request.getClubId());
        activitiesService.add(activity);
    }

    @Transactional
    public void submitCompletion(AuthUser authUser, String activityId, SubmitActivityCompletionRequest request) {
        Activities activity = getManagedActivity(authUser, activityId);
        Integer currentStatus = valueOrDefault(activity.getCompletionStatus(), 0);
        if (currentStatus == 1) {
            throw new IllegalArgumentException("该活动已提交结项，等待指导老师确认");
        }
        if (currentStatus == 2) {
            throw new IllegalArgumentException("该活动已完成结项确认");
        }
        activity.setCompletionStatus(1);
        activity.setCompletionSummary(request.getSummary());
        activity.setCompletionImages(String.join(",", request.getImages()));
        activity.setCompletionSubmittedBy(authUser.getUserId());
        activity.setCompletionSubmittedAt(DateUtils.getNowDate());
        activity.setCompletionReviewComment("");
        activity.setCompletionReviewedBy("");
        activity.setCompletionReviewedAt("");
        activitiesDao.updateById(activity);
    }

    @Transactional
    public void reviewCompletion(AuthUser authUser, String activityId, ReviewActivityCompletionRequest request) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        AuthRole role = AuthRole.fromType(authUser.getType());
        if (role != AuthRole.TEACHER && role != AuthRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("只有指导老师可以确认活动结项");
        }
        if (role == AuthRole.TEACHER) {
            clubPermissionService.ensureManager(authUser, activity.getTeamId());
        }
        if (valueOrDefault(activity.getCompletionStatus(), 0) != 1) {
            throw new IllegalArgumentException("当前活动还没有待确认的结项材料");
        }
        activity.setCompletionStatus(request.isApproved() ? 2 : 3);
        activity.setCompletionReviewComment(request.getComment());
        activity.setCompletionReviewedBy(authUser.getUserId());
        activity.setCompletionReviewedAt(DateUtils.getNowDate());
        activitiesDao.updateById(activity);
    }

    @Transactional
    public void signup(AuthUser authUser, String activityId) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        if (!activeLogsService.isActive(activityId, authUser.getUserId())) {
            throw new IllegalArgumentException("你已经报名过该活动");
        }
        if (activity.getCapacity() != null && activity.getCapacity() > 0 && activity.getTotal() >= activity.getCapacity()) {
            throw new IllegalArgumentException("活动人数已满");
        }
        ActiveLogs activeLogs = new ActiveLogs();
        activeLogs.setId(IDUtils.makeIDByCurrent());
        activeLogs.setCreateTime(DateUtils.getNowDate());
        activeLogs.setActiveId(activityId);
        activeLogs.setUserId(authUser.getUserId());
        activeLogsService.add(activeLogs);
    }

    @Transactional
    public void cancelSignup(AuthUser authUser, String activityId) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        if (activeLogsService.isActive(activityId, authUser.getUserId())) {
            throw new IllegalArgumentException("你还没有报名该活动");
        }
        if (clubPermissionService.isClubManager(authUser, activity.getTeamId())) {
            throw new IllegalArgumentException("社团管理者默认保留活动主理身份，不支持取消");
        }
        activeLogsService.cancelSignup(activityId, authUser.getUserId());
    }

    @Transactional(readOnly = true)
    public List<ActivitySignupResponse> listSignups(AuthUser authUser, String activityId) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色无权查看报名名单");
        }
        clubPermissionService.ensureManager(authUser, activity.getTeamId());

        List<Map<String, Object>> records = activeLogsService.getListByActiveId(activityId);
        List<ActivitySignupResponse> responses = new ArrayList<>();
        for (Map<String, Object> record : records) {
            responses.add(new ActivitySignupResponse(
                    valueAsString(record.get("id")),
                    valueAsString(record.get("createTime")),
                    valueAsString(record.get("activeId")),
                    valueAsString(record.get("userId")),
                    valueAsString(record.get("userName")),
                    valueAsString(record.get("userGender")),
                    valueAsString(record.get("userPhone"))
            ));
        }
        return responses;
    }

    private Activities getManagedActivity(AuthUser authUser, String activityId) {
        Activities activity = activitiesDao.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        clubPermissionService.ensureManager(authUser, activity.getTeamId());
        return activity;
    }

    private ActivitySummaryResponse toResponse(String id,
                                               String name,
                                               String summary,
                                               String detail,
                                               String requirement,
                                               Integer total,
                                               String activeTime,
                                               String location,
                                               Integer capacity,
                                               String coverImage,
                                               String clubId,
                                               String clubName,
                                               boolean joined,
                                               boolean manageable,
                                               Integer completionStatus,
                                               String completionSummary,
                                               String completionImages,
                                               String completionSubmittedAt,
                                               String completionReviewComment,
                                               String completionReviewedAt,
                                               AuthUser authUser) {
        int status = valueOrDefault(completionStatus, 0);
        boolean canReview = canReviewCompletion(authUser, manageable);
        boolean canSubmit = manageable && status != 1 && status != 2;
        return new ActivitySummaryResponse(
                id,
                name,
                summary,
                detail,
                requirement,
                total,
                activeTime,
                location,
                capacity,
                coverImage,
                clubId,
                clubName,
                joined,
                manageable,
                status,
                completionStatusLabel(status),
                completionSummary == null ? "" : completionSummary,
                splitImages(completionImages),
                completionSubmittedAt == null ? "" : completionSubmittedAt,
                completionReviewComment == null ? "" : completionReviewComment,
                completionReviewedAt == null ? "" : completionReviewedAt,
                canSubmit,
                canReview
        );
    }

    private boolean canReviewCompletion(AuthUser authUser, boolean manageable) {
        AuthRole role = AuthRole.fromType(authUser.getType());
        if (role == AuthRole.SUPER_ADMIN) {
            return true;
        }
        return role == AuthRole.TEACHER && manageable;
    }

    private String completionStatusLabel(int status) {
        switch (status) {
            case 1:
                return "待教师确认";
            case 2:
                return "已确认";
            case 3:
                return "需补充";
            default:
                return "进行中";
        }
    }

    private List<String> splitImages(String completionImages) {
        if (!StringUtils.hasText(completionImages)) {
            return Collections.emptyList();
        }
        return Arrays.stream(completionImages.split(","))
                .filter(StringUtils::hasText)
                .collect(java.util.stream.Collectors.toList());
    }

    private String valueAsString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Integer valueAsInteger(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value == null) return 0;
        return Integer.parseInt(String.valueOf(value));
    }

    private Integer valueOrDefault(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String defaultCoverForClub(String clubId) {
        String[] covers = {
                "/covers/campus-01.svg",
                "/covers/campus-02.svg",
                "/covers/campus-03.svg",
                "/covers/campus-04.svg",
                "/covers/campus-05.svg",
                "/covers/campus-06.svg",
                "/covers/campus-07.svg",
                "/covers/campus-08.svg"
        };
        int index = Math.abs(clubId.hashCode()) % covers.length;
        return covers[index];
    }
}
