package com.rabbiter.association.publicapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.activity.ActivityFacade;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubApplicationFacade;
import com.rabbiter.association.dao.UsersDao;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.forum.ForumFacade;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.notice.NoticeFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final ClubApplicationFacade clubApplicationFacade;
    private final ActivityFacade activityFacade;
    private final NoticeFacade noticeFacade;
    private final ForumFacade forumFacade;
    private final UsersDao usersDao;

    public PublicController(ClubApplicationFacade clubApplicationFacade,
                            ActivityFacade activityFacade,
                            NoticeFacade noticeFacade,
                            ForumFacade forumFacade,
                            UsersDao usersDao) {
        this.clubApplicationFacade = clubApplicationFacade;
        this.activityFacade = activityFacade;
        this.noticeFacade = noticeFacade;
        this.forumFacade = forumFacade;
        this.usersDao = usersDao;
    }

    @GetMapping("/health")
    public R health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "association-manager");
        return R.successData(data);
    }

    @GetMapping("/health/db")
    public R dbHealth() {
        Long totalUsers = usersDao.selectCount(new QueryWrapper<Users>());
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("users", totalUsers);
        return R.successData(data);
    }

    @GetMapping("/clubs")
    public R clubs() {
        return R.successData(clubApplicationFacade.listClubs(new AuthUser("", "", "游客", 2, "guest")));
    }

    @GetMapping("/activities")
    public R activities() {
        return R.successData(activityFacade.listActivities(new AuthUser("", "", "游客", 2, "guest")));
    }

    @GetMapping("/notices")
    public R notices() {
        return R.successData(noticeFacade.list(new AuthUser("", "", "游客", 0, "super-admin")));
    }

    @GetMapping("/forum/posts")
    public R forumPosts() {
        return R.successData(forumFacade.list(new AuthUser("", "", "游客", 2, "guest")));
    }

    @GetMapping("/forum/feed")
    public R forumFeed() {
        return R.successData(forumFacade.feed(new AuthUser("", "", "游客", 2, "guest")));
    }
}
