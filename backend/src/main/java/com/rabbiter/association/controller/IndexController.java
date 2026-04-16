package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private NoticesService noticesService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/sys/notices")
    @ResponseBody
    public R getNoticeList(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        if (user.getType() == 0) {
            List<Notices> list = noticesService.getSysNotices();
            return R.successData(list);
        } else if (user.getType() == 1 || user.getType() == 3) {
            List<Notices> list = noticesService.getManNotices(user.getId());
            return R.successData(list);
        } else {
            List<Notices> list = noticesService.getMemNotices(user.getId());
            return R.successData(list);
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public R login(String userName, String passWord, HttpSession session) {
        log.info("Legacy login request received, userName={}", userName);

        Users user = usersService.getUserByUserName(userName);
        if (user == null) {
            return R.error("输入的用户名不存在");
        }

        if (!matchesPassword(passWord, user.getPassWord())) {
            return R.error("输入的密码错误");
        }

        String token = IDUtils.makeIDByUUID();
        cacheHandle.addUserCache(token, user.getId());

        if (!isEncoded(user.getPassWord())) {
            user.setPassWord(passwordEncoder.encode(passWord));
            usersService.update(user);
        }

        return R.success("登录成功", token);
    }

    @RequestMapping("/exit")
    @ResponseBody
    public R exit(String token) {
        log.info("Legacy logout request received");
        cacheHandle.removeUserCache(token);
        return R.success();
    }

    @GetMapping("/info")
    @ResponseBody
    public R info(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        return R.successData(user);
    }

    @PostMapping("/info")
    @ResponseBody
    public R info(Users user) {
        log.info("Update user profile, userId={}", user.getId());
        usersService.update(user);
        return R.success();
    }

    @RequestMapping("/checkPwd")
    @ResponseBody
    public R checkPwd(String oldPwd, String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (matchesPassword(oldPwd, user.getPassWord())) {
            return R.success();
        }
        return R.warn("原始密码和输入密码不一致");
    }

    @PostMapping("/pwd")
    @ResponseBody
    public R pwd(String token, String password) {
        log.info("Update current user password");
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        user.setPassWord(passwordEncoder.encode(password));
        usersService.update(user);
        return R.success();
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (isEncoded(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword.trim());
    }

    private boolean isEncoded(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
