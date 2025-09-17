package com.ssafy.backend.domain.meet.service.meet.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.follow.entity.Follow;
import com.ssafy.backend.domain.follow.repository.FollowRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.push.service.push.PushService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetPushManager {
    private final PushService pushService;
    private final FollowRepository followRepository;

    @Value("${neighbrew.full.url}")
    private String neighbrewUrl;

    public void notifyFollowersAboutMeetCreation(User hostUser, Meet createdMeet) {
        List<Follow> followers = findFollowersByUserId(hostUser.getUserId());
        followers.forEach(follower -> pushService.send(hostUser, follower.getFollower(), PushType.MEETCREATED,
                                                       hostUser.getNickname() + "님께서 모임("
                                                       + createdMeet.getMeetName() + ")을 생성했습니다.",
                                                       neighbrewUrl + "/meet/" + createdMeet.getMeetId()));
    }

    public void notifyMeetUpdate(User host, Meet meet, List<MeetUser> meetUsers) {
        meetUsers.stream().filter(user -> !user.getUser().getUserId().equals(host.getUserId())).forEach(
                user -> pushService.send(host, user.getUser(), PushType.MEETMODIFIDE,
                                         "모임( " + meet.getMeetName() + ")의 내용이 수정되었습니다. 확인해 주세요.",
                                         neighbrewUrl + "/meet/" + meet.getMeetId()));
    }

    public void notifyMeetDeletion(Meet deleteMeet, List<MeetUser> meetUsers) {
        meetUsers.stream().filter(user -> !user.getUser().getUserId().equals(deleteMeet.getHost().getUserId()))
                 .forEach(user -> pushService.send(deleteMeet.getHost(), user.getUser(), PushType.MEETDELETED,
                                                   deleteMeet.getHost().getNickname() + "님 께서 생성한 모임" + "("
                                                   + deleteMeet.getMeetName() + ")이 삭제되었습니다.",
                                                   neighbrewUrl + "/meet"));
    }

    public void notifyMeetApplication(User attendUser, User host, String meetName, Long meetId) {
        pushService.send(attendUser, host, PushType.MEETACCESS,
                         attendUser.getNickname() + "님께서 " + meetName + "모임에 참여하고 싶어 합니다.",
                         neighbrewUrl + "/meet/" + meetId);
    }

    public void notifyMeetAcceptance(User host, User manageUser, String meetName, Long meetId) {
        pushService.send(host, manageUser, PushType.MEETACCESS,
                         "회원님께서 모임(" + meetName + ")참여 되셨습니다.\n 즐거운 시간 되세요.", "meet/" + meetId);
    }

    public void notifyMeetRejection(User host, User manageUser, String meetName) {
        pushService.send(host, manageUser, PushType.MEETREJECT, "회원님께서 모임(" + meetName + ")참여에 거절당했습니다.",
                         "meet");
    }

    public void notifyMeetEvaluation(Meet meet, List<MeetUser> findUsers) {
        findUsers.forEach(users -> pushService.send(meet.getHost(), users.getUser(), PushType.MEETEVALUATION,
                                                    "모임(" + meet.getMeetName() + ")이 종료 되었습니다. 평가를 진행해 주세요.",
                                                    neighbrewUrl + "/rating/" + meet.getMeetId()));
    }

    public void deletePushLog(PushType pushType, Long senderId, Long receiverId) {
        pushService.deletePushLog(pushType, senderId, receiverId);
    }

    private List<Follow> findFollowersByUserId(Long userId) {
        return followRepository.findByFollowingUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("팔로잉 정보를 찾을 수 없습니다."));
    }
}