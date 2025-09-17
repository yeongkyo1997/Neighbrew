package com.ssafy.backend.domain.evaluation.service.evaluation.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.repository.EvaluationRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EvaluationValidator {
    private static final String EVALUATION_ERR_SELF = "자기 자신을 평가 할 수 없습니다.";
    private static final String ERR_NOT_FOUND = "%s를 찾을 수 없습니다.";
    private static final String ERR_ALREADY_EVALUATED = "이미 평가한 유저 입니다.";
    private static final String ERR_INVALID_MEET_ID = "모임 ID 값이 올바르지 않습니다.";
    private static final String ERR_NO_USERS = "평가 가능한 유저가 없습니다.";

    private final UserRepository userRepository;
    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;
    private final EvaluationRepository evaluationRepository;

    public void validateSelfEvaluation(EvaluationRequestDto evaluationRequestDto, Long userId) {
        if (Objects.equals(userId, evaluationRequestDto.getReviewer())) {
            throw new IllegalArgumentException(EVALUATION_ERR_SELF);
        }
    }

    public User validateAndGetRatedUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "ratedUser")));
    }

    public User validateAndGetReviewer(Long reviewerId) {
        return userRepository.findById(reviewerId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "reviewer")));
    }

    public Meet validateAndGetMeet(Long meetId) {
        return meetRepository.findById(meetId).orElseThrow(
                () -> new IllegalStateException(String.format(ERR_NOT_FOUND, "meet")));
    }

    public void validateNotAlreadyEvaluated(User ratedUser, User reviewer, Meet meet) {
        if (evaluationRepository.existsByRatedUserAndReviewerAndMeet(ratedUser, reviewer, meet)) {
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
}