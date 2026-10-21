package com.ssafy.backend.domain.meet.service.meet.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.meet.dto.MeetUserDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetParticipantManager {
    private final MeetUserService meetUserService;

    public void applyMeet(Long userId, MeetUserDto meetUserDto, Meet attendMeet, User attendUser) {
        validateParticipation(userId, meetUserDto);
        meetUserService.saveMeetUser(attendMeet, attendUser, Status.APPLY);
    }

    private void validateParticipation(Long userId, MeetUserDto meetUserDto) {
        if (meetUserDto.getMeetRequestDto().getNowParticipants() >= meetUserDto.getMeetRequestDto()
                                                                               .getMaxParticipants()) {
            throw new IllegalArgumentException("해당 모임에 참여 인원이 가득 찼습니다.");
        }

        for (User user : meetUserDto.getUsers()) {
            if (userId.equals(user.getUserId())) {
                throw new IllegalArgumentException("이미 참여하신 모임 입니다.");
            }
        }
    }

    public void applyCancelMeet(Long userId, Long meetId) {
        Status applyUserStatus = meetUserService.findUserStatus(userId, meetId);
        if (applyUserStatus != Status.APPLY) {
            throw new IllegalArgumentException("가입신청중인 유저만 모임 신청을 취소할 수 있습니다.");
        }
        meetUserService.deleteExitUser(userId, meetId, Status.APPLY);
    }

    public void exitMeet(Long userId, Long meetId) {
        Status applyUserStatus = meetUserService.findUserStatus(userId, meetId);
        if (applyUserStatus == Status.HOST) {
            throw new IllegalArgumentException("죄송합니다.. 방장님은 나가실 수 없으십니다. 모임 삭제를 요청하세요.");
        }
        if (applyUserStatus != Status.GUEST) {
            throw new IllegalArgumentException("모임에 참여중인 유저만 나갈 수 있습니다.");
        }
        meetUserService.deleteExitUser(userId, meetId, Status.GUEST);
    }

    public String manageMeet(Long userId, Long meetId, boolean applyResult) {
        if (applyResult) {
            meetUserService.updateMeetStatus(userId, meetId, Status.GUEST);
            return userId + "유저 " + meetId + "모임 신청 승인";
        } else {
            meetUserService.deleteExitUser(userId, meetId, Status.APPLY);
            return userId + "유저 " + meetId + "모임 신청 거절";
        }
    }

    public void deleteMeetUsers(Meet meet) {
        meetUserService.deleteMeetUser(meet);
    }
}