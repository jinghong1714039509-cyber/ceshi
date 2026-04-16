package com.rabbiter.association.activity;

import com.rabbiter.association.activity.dto.CreateActivityRequest;
import com.rabbiter.association.activity.dto.ReviewActivityCompletionRequest;
import com.rabbiter.association.activity.dto.SubmitActivityCompletionRequest;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ActivityRestController {

    private final ActivityFacade activityFacade;

    public ActivityRestController(ActivityFacade activityFacade) {
        this.activityFacade = activityFacade;
    }

    @GetMapping("/activities")
    public R listActivities(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(activityFacade.listActivities(authUser));
    }

    @GetMapping("/activities/{activityId}")
    public R activity(@PathVariable String activityId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(activityFacade.getActivity(authUser, activityId));
    }

    @PostMapping("/activities")
    public R createActivity(@Validated @RequestBody CreateActivityRequest request, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        activityFacade.createActivity(authUser, request);
        return R.successMsg("活动已创建");
    }

    @PostMapping("/activities/{activityId}/completion")
    public R submitCompletion(@PathVariable String activityId,
                              @Validated @RequestBody SubmitActivityCompletionRequest request,
                              Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        activityFacade.submitCompletion(authUser, activityId, request);
        return R.successMsg("活动结项材料已提交");
    }

    @PostMapping("/activities/{activityId}/completion/review")
    public R reviewCompletion(@PathVariable String activityId,
                              @Validated @RequestBody ReviewActivityCompletionRequest request,
                              Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        activityFacade.reviewCompletion(authUser, activityId, request);
        return R.successMsg("活动结项审核已完成");
    }

    @PostMapping("/activities/{activityId}/signup")
    public R signup(@PathVariable String activityId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        activityFacade.signup(authUser, activityId);
        return R.successMsg("报名成功");
    }

    @DeleteMapping("/activities/{activityId}/signup")
    public R cancelSignup(@PathVariable String activityId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        activityFacade.cancelSignup(authUser, activityId);
        return R.successMsg("已取消报名");
    }

    @GetMapping("/activities/{activityId}/signups")
    public R signups(@PathVariable String activityId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(activityFacade.listSignups(authUser, activityId));
    }
}
