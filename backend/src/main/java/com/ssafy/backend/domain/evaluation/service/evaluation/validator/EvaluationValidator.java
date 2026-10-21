package com.ssafy.backend.domain.evaluation.service.evaluation.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.repository.EvaluationRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EvaluationValidator {
    private static final String EVALUATION_ERR_SELF = "자기 자신을 평가 할 수 없습니다.";
    private static final String ERR_NOT_FOUND = "%s를 찾을 수 없습니다.";
    private static final String ERR_ALREADY_EVALUATED = "이미 평가한 유저 입니다.";
    private static final String ERR_INVALID_MEET_ID = "모임 ID 값이 올바르지 않습니다.";
    private static final String ERR_NO_USERS = "평가 가능한 유저가 없습니다.";
    private static final String ERR_NOT_PARTICIPANT = "모임에 참여한 사용자끼리만 평가할 수 있습니다.";
    private static final String ERR_MEET_NOT_ENDED = "종료된 모임만 평가할 수 있습니다.";

    private final UserRepository userRepository;
    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;
    private final EvaluationRepository evaluationRepository;

    public void validateSelfEvaluation(EvaluationRequestDto evaluationRequestDto, Long userId) {
        if (Objects.equals(userId, evaluationRequestDto.getReviewer())) {
            throw new IllegalArgumentException(EVALUATION_ERR_SELF);
        }
    }

    // 평가자와 평가 대상 모두 해당 모임의 HOST 또는 GUEST여야 한다 (신청만 한 APPLY는 제외)
    public void validateParticipants(Long meetId, Long evaluatorId, Long targetId) {
        if (!isMember(meetUserRepository.findStatusByUserIdAndMeetId(evaluatorId, meetId))
            || !isMember(meetUserRepository.findStatusByUserIdAndMeetId(targetId, meetId))) {
            throw new ForbiddenException(ERR_NOT_PARTICIPANT);
        }
    }

    public User validateAndGetEvaluator(Long evaluatorId) {
        return userRepository.findById(evaluatorId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "evaluator")));
    }

    public User validateAndGetTarget(Long targetId) {
        return userRepository.findById(targetId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "target")));
    }

    public Meet validateAndGetMeet(Long meetId) {
        return meetRepository.findById(meetId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "meet")));
    }

    // 프론트도 meetStatus가 END일 때만 평가 화면으로 보낸다
    public void validateMeetEnded(Meet meet) {
        if (meet.getMeetStatus() != MeetStatus.END) {
            throw new IllegalArgumentException(ERR_MEET_NOT_ENDED);
        }
    }

    // Evaluation 엔티티는 ratedUser 컬럼에 평가자, reviewer 컬럼에 평가 대상을 저장한다
    public void validateNotAlreadyEvaluated(User evaluator, User target, Meet meet) {
        if (evaluationRepository.existsByRatedUserAndReviewerAndMeet(evaluator, target, meet)) {
            throw new IllegalArgumentException(ERR_ALREADY_EVALUATED);
        }
    }

    public Long validateAndGetTotalUsers(Long meetId) {
        Long totalUsers = meetUserRepository.countByMeetMeetId(meetId).orElseThrow(
                () -> new IllegalArgumentException(ERR_INVALID_MEET_ID));

        if (totalUsers <= 1L) {
            throw new IllegalArgumentException(ERR_NO_USERS);
        }

        return totalUsers;
    }

    private boolean isMember(Status status) {
        return status == Status.HOST || status == Status.GUEST;
    }
}
