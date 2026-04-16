package com.rabbiter.association.profile;

import com.rabbiter.association.activity.ActivityFacade;
import com.rabbiter.association.activity.dto.ActivitySummaryResponse;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.club.ClubApplicationFacade;
import com.rabbiter.association.club.dto.ClubSummaryResponse;
import com.rabbiter.association.club.dto.JoinApplicationResponse;
import com.rabbiter.association.profile.dto.ProfileOverviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileFacade {

    private final ClubApplicationFacade clubApplicationFacade;
    private final ActivityFacade activityFacade;

    public ProfileFacade(ClubApplicationFacade clubApplicationFacade, ActivityFacade activityFacade) {
        this.clubApplicationFacade = clubApplicationFacade;
        this.activityFacade = activityFacade;
    }

    @Transactional(readOnly = true)
    public ProfileOverviewResponse overview(AuthUser authUser) {
        List<ClubSummaryResponse> myClubs = clubApplicationFacade.listClubs(authUser).stream()
                .filter(ClubSummaryResponse::isJoined)
                .collect(Collectors.toList());

        List<ActivitySummaryResponse> myActivities = activityFacade.listActivities(authUser).stream()
                .filter(ActivitySummaryResponse::isJoined)
                .collect(Collectors.toList());

        List<JoinApplicationResponse> myApplications = clubApplicationFacade.listMine(authUser);

        return new ProfileOverviewResponse(
                authUser.getUserId(),
                authUser.getUserName(),
                authUser.getDisplayName(),
                authUser.getRole(),
                myClubs,
                myActivities,
                myApplications
        );
    }
}
