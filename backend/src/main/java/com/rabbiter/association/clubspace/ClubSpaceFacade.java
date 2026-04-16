package com.rabbiter.association.clubspace;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubPermissionService;
import com.rabbiter.association.clubspace.dto.ClubMessageResponse;
import com.rabbiter.association.clubspace.dto.ClubPostResponse;
import com.rabbiter.association.clubspace.dto.ClubSpaceResponse;
import com.rabbiter.association.clubspace.dto.CreateClubMessageRequest;
import com.rabbiter.association.clubspace.dto.CreateClubPostRequest;
import com.rabbiter.association.dao.ActivitiesDao;
import com.rabbiter.association.dao.ClubMessagesDao;
import com.rabbiter.association.dao.ClubPostsDao;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.dao.TeamTypesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.dao.UsersDao;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.ClubMessage;
import com.rabbiter.association.entity.ClubPost;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.TeamTypes;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.notice.dto.NoticeResponse;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClubSpaceFacade {

    private final TeamsDao teamsDao;
    private final TeamTypesDao teamTypesDao;
    private final UsersDao usersDao;
    private final MembersDao membersDao;
    private final ActivitiesDao activitiesDao;
    private final NoticesDao noticesDao;
    private final ClubPostsDao clubPostsDao;
    private final ClubMessagesDao clubMessagesDao;
    private final ClubPermissionService clubPermissionService;

    public ClubSpaceFacade(TeamsDao teamsDao,
                           TeamTypesDao teamTypesDao,
                           UsersDao usersDao,
                           MembersDao membersDao,
                           ActivitiesDao activitiesDao,
                           NoticesDao noticesDao,
                           ClubPostsDao clubPostsDao,
                           ClubMessagesDao clubMessagesDao,
                           ClubPermissionService clubPermissionService) {
        this.teamsDao = teamsDao;
        this.teamTypesDao = teamTypesDao;
        this.usersDao = usersDao;
        this.membersDao = membersDao;
        this.activitiesDao = activitiesDao;
        this.noticesDao = noticesDao;
        this.clubPostsDao = clubPostsDao;
        this.clubMessagesDao = clubMessagesDao;
        this.clubPermissionService = clubPermissionService;
    }

    @Transactional(readOnly = true)
    public ClubSpaceResponse getSpace(AuthUser authUser, String clubId) {
        clubPermissionService.ensureMember(authUser, clubId);

        Teams club = getClub(clubId);
        Users manager = usersDao.selectById(club.getManager());
        TeamTypes type = teamTypesDao.selectById(club.getTypeId());
        int memberCount = membersDao.selectCount(new QueryWrapper<com.rabbiter.association.entity.Members>()
                .eq("team_id", clubId)).intValue();

        List<ActivitySummaryResponse> activities = activitiesDao.selectList(new QueryWrapper<Activities>()
                        .eq("team_id", clubId)
                        .orderByDesc("active_time"))
                .stream()
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
                        activity.getTeamId(),
                        club.getName(),
                        false,
                        clubPermissionService.isClubManager(authUser, clubId),
                        activity.getCompletionStatus() == null ? 0 : activity.getCompletionStatus(),
                        "进行中",
                        activity.getCompletionSummary(),
                        java.util.Collections.emptyList(),
                        activity.getCompletionSubmittedAt(),
                        activity.getCompletionReviewComment(),
                        activity.getCompletionReviewedAt(),
                        clubPermissionService.isClubManager(authUser, clubId),
                        false
                ))
                .sorted(Comparator.comparing(ActivitySummaryResponse::getActiveTime).reversed())
                .limit(6)
                .collect(Collectors.toList());

        List<NoticeResponse> notices = noticesDao.selectList(new QueryWrapper<Notices>()
                        .eq("team_id", clubId)
                        .orderByDesc("create_time"))
                .stream()
                .limit(4)
                .map(notice -> new NoticeResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getDetail(),
                        notice.getCreateTime(),
                        notice.getTeamId(),
                        club.getName(),
                        false
                ))
                .collect(Collectors.toList());

        List<ClubPostResponse> posts = clubPostsDao.listByTeamId(clubId).stream()
                .map(row -> new ClubPostResponse(
                        valueAsString(row.get("id")),
                        valueAsString(row.get("title")),
                        valueAsString(row.get("content")),
                        valueAsInteger(row.get("pinned")) == 1,
                        valueAsString(row.get("createTime")),
                        authorName(row)
                ))
                .collect(Collectors.toList());

        List<ClubMessageResponse> messages = clubMessagesDao.listRecentByTeamId(clubId).stream()
                .map(row -> new ClubMessageResponse(
                        valueAsString(row.get("id")),
                        valueAsString(row.get("userId")),
                        valueAsString(row.get("content")),
                        valueAsString(row.get("createTime")),
                        authorName(row)
                ))
                .sorted(Comparator.comparing(ClubMessageResponse::getCreateTime))
                .collect(Collectors.toList());

        return new ClubSpaceResponse(
                club.getId(),
                club.getName(),
                type == null ? "未分类" : type.getName(),
                manager == null ? "未设置" : displayName(manager),
                memberCount,
                clubPermissionService.isClubManager(authUser, clubId),
                activities,
                notices,
                posts,
                messages
        );
    }

    @Transactional
    public void createPost(AuthUser authUser, String clubId, CreateClubPostRequest request) {
        clubPermissionService.ensureMember(authUser, clubId);
        ClubPost post = new ClubPost();
        post.setId(IDUtils.makeIDByCurrent());
        post.setTeamId(clubId);
        post.setUserId(authUser.getUserId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPinned(clubPermissionService.isClubManager(authUser, clubId) ? 1 : 0);
        post.setCreateTime(DateUtils.getNowDate());
        clubPostsDao.insert(post);
    }

    @Transactional
    public void createMessage(AuthUser authUser, String clubId, CreateClubMessageRequest request) {
        clubPermissionService.ensureMember(authUser, clubId);
        ClubMessage message = new ClubMessage();
        message.setId(IDUtils.makeIDByCurrent());
        message.setTeamId(clubId);
        message.setUserId(authUser.getUserId());
        message.setContent(request.getContent());
        message.setCreateTime(DateUtils.getNowDate());
        clubMessagesDao.insert(message);
    }

    private Teams getClub(String clubId) {
        Teams club = teamsDao.selectById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("社团不存在");
        }
        return club;
    }

    private String displayName(Users user) {
        return StringUtils.hasText(user.getName()) ? user.getName() : user.getUserName();
    }

    private String authorName(Map<String, Object> row) {
        String authorName = valueAsString(row.get("authorName"));
        return StringUtils.hasText(authorName) ? authorName : valueAsString(row.get("authorUserName"));
    }

    private String valueAsString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Integer valueAsInteger(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
