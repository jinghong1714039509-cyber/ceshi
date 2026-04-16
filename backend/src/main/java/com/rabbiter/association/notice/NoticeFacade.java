package com.rabbiter.association.notice;

import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubPermissionService;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.notice.dto.CreateNoticeRequest;
import com.rabbiter.association.notice.dto.NoticeResponse;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeFacade {

    private final NoticesService noticesService;
    private final TeamsDao teamsDao;
    private final ClubPermissionService clubPermissionService;

    public NoticeFacade(NoticesService noticesService, TeamsDao teamsDao, ClubPermissionService clubPermissionService) {
        this.noticesService = noticesService;
        this.teamsDao = teamsDao;
        this.clubPermissionService = clubPermissionService;
    }

    @Transactional(readOnly = true)
    public List<NoticeResponse> list(AuthUser authUser) {
        List<Notices> notices;
        if (clubPermissionService.isSuperAdmin(authUser)) {
            notices = new ArrayList<>();
            notices.addAll(noticesService.getSysNotices());
            notices.addAll(noticesService.getManNotices(authUser.getUserId()));
        } else if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            notices = noticesService.getMemNotices(authUser.getUserId());
        } else {
            notices = noticesService.getManNotices(authUser.getUserId());
        }

        List<NoticeResponse> responses = new ArrayList<>();
        for (Notices notice : notices) {
            Teams team = StringUtils.hasText(notice.getTeamId()) ? teamsDao.selectById(notice.getTeamId()) : null;
            responses.add(new NoticeResponse(
                    notice.getId(),
                    notice.getTitle(),
                    notice.getDetail(),
                    notice.getCreateTime(),
                    notice.getTeamId(),
                    team == null ? "" : team.getName(),
                    !StringUtils.hasText(notice.getTeamId())
            ));
        }
        return responses;
    }

    @Transactional
    public void create(AuthUser authUser, CreateNoticeRequest request) {
        if (AuthRole.fromType(authUser.getType()) == AuthRole.STUDENT) {
            throw new IllegalArgumentException("当前角色不能发布公告");
        }

        Notices notice = new Notices();
        notice.setId(IDUtils.makeIDByCurrent());
        notice.setTitle(request.getTitle());
        notice.setDetail(request.getDetail());
        notice.setCreateTime(DateUtils.getNowDate("yyyy-MM-dd"));

        if (clubPermissionService.isSuperAdmin(authUser) && !StringUtils.hasText(request.getTeamId())) {
            notice.setTeamId(null);
        } else {
            if (!StringUtils.hasText(request.getTeamId())) {
                throw new IllegalArgumentException("请选择所属社团");
            }
            Teams team = teamsDao.selectById(request.getTeamId());
            if (team == null) {
                throw new IllegalArgumentException("社团不存在");
            }
            clubPermissionService.ensureManager(authUser, request.getTeamId());
            notice.setTeamId(request.getTeamId());
        }

        noticesService.add(notice);
    }
}
