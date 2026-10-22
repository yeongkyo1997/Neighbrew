package com.ssafy.backend.domain.meet.service.meet.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.follow.entity.Follow;
import com.ssafy.backend.domain.follow.repository.FollowRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.push.service.push.PushMessage;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetPushManager {
    private final PushService pushService;
    private final FollowRepository followRepository;

    public void notifyFollowersAboutMeetCreation(User hostUser, Meet createdMeet) {
        List<Follow> followers = findFollowersByUserId(hostUser.getUserId());
        PushMessage message = PushMessage.meetCreated(hostUser.getNickname(), createdMeet.getMeetName(),
                                                      createdMeet.getMeetId());
        followers.forEach(follower -> pushService.send(hostUser, follower.getFollower(), message));
    }

    public void notifyMeetUpdate(User host, Meet meet, List<MeetUser> meetUsers) {
        PushMessage message = PushMessage.meetModified(meet.getMeetName(), meet.getMeetId());
        meetUsers.stream().filter(user -> !user.getUser().getUserId().equals(host.getUserId()))
                 .forEach(user -> pushService.send(host, user.getUser(), message));
    }

    public void notifyMeetDeletion(Meet deleteMeet, List<MeetUser> meetUsers) {
        PushMessage message = PushMessage.meetDeleted(deleteMeet.getHost().getNickname(), deleteMeet.getMeetName());
        meetUsers.stream().filter(user -> !user.getUser().getUserId().equals(deleteMeet.getHost().getUserId()))
                 .forEach(user -> pushService.send(deleteMeet.getHost(), user.getUser(), message));
    }

    public void notifyMeetApplication(User attendUser, User host, String meetName, Long meetId) {
        pushService.send(attendUser, host, PushMessage.meetApplied(attendUser.getNickname(), meetName, meetId));
    }

    public void notifyMeetAcceptance(User host, User manageUser, String meetName, Long meetId) {
        pushService.send(host, manageUser, PushMessage.meetAccepted(meetName, meetId));
    }

    public void notifyMeetRejection(User host, User manageUser, String meetName) {
        pushService.send(host, manageUser, PushMessage.meetRejected(meetName));
    }

    public void notifyMeetEvaluation(Meet meet, List<MeetUser> findUsers) {
        PushMessage message = PushMessage.meetEvaluation(meet.getMeetName(), meet.getMeetId());
        findUsers.forEach(users -> pushService.send(meet.getHost(), users.getUser(), message));
    }

    public void deletePushLog(PushType pushType, Long senderId, Long receiverId) {
        pushService.deletePushLog(pushType, senderId, receiverId);
    }

    private List<Follow> findFollowersByUserId(Long userId) {
        return followRepository.findByFollowingUserId(userId);
    }
}