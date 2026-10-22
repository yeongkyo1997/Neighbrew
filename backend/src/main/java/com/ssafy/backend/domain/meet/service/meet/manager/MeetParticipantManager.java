package com.ssafy.backend.domain.meet.service.meet.manager;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetParticipantManager {
    private final MeetUserService meetUserService;

    public void applyMeet(Meet attendMeet, User attendUser) {
        validateParticipation(attendMeet, attendUser);
        meetUserService.saveMeetUser(attendMeet, attendUser, Status.APPLY);
    }

    // 정원은 승인할 때 Meet.addParticipant가 다시 확인하므로, 여기서는 빨리 실패시키는 용도다.
    private void validateParticipation(Meet attendMeet, User attendUser) {
        if (attendMeet.getNowParticipants() >= attendMeet.getMaxParticipants()) {
            throw new IllegalArgumentException("해당 모임에 참여 인원이 가득 찼습니다.");
        }
        checkEligibility(attendMeet, attendUser);
        // HOST, GUEST, APPLY 중 어떤 상태든 있으면 이미 참여(신청)한 사용자다
        if (meetUserService.findUserStatus(attendUser.getUserId(), attendMeet.getMeetId()) != null) {
            throw new IllegalArgumentException("이미 참여하신 모임 입니다.");
        }
    }

    // 신청 후 승인 전까지 간수치나 제한이 바뀔 수 있으므로 승인할 때도 호출한다.
    public void checkEligibility(Meet meet, User user) {
        meet.validateEligibility(user, LocalDate.now());
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