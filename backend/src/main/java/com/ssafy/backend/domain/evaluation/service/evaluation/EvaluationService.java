package com.ssafy.backend.domain.evaluation.service.evaluation;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.service.evaluation.manager.EvaluationManager;
import com.ssafy.backend.domain.evaluation.service.evaluation.manager.EvaluationScoreManager;
import com.ssafy.backend.domain.evaluation.service.evaluation.validator.EvaluationValidator;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationValidator validator;
    private final EvaluationScoreManager scoreManager;
    private final EvaluationManager evaluationManager;

    @Transactional
    public String calculateScoreByMeetId(EvaluationRequestDto evaluationRequestDto, Long userId) {
        validator.validateSelfEvaluation(evaluationRequestDto, userId);

        User ratedUser = validator.validateAndGetRatedUser(userId);
        User reviewer = validator.validateAndGetReviewer(evaluationRequestDto.getReviewer());
        Meet meet = validator.validateAndGetMeet(evaluationRequestDto.getMeetId());

        validator.validateNotAlreadyEvaluated(ratedUser, reviewer, meet);

        Long totalUsers = validator.validateAndGetTotalUsers(evaluationRequestDto.getMeetId());

        scoreManager.updateReviewerScore(reviewer, evaluationRequestDto.getEvaluationType(), totalUsers);
        evaluationManager.saveEvaluation(evaluationRequestDto, reviewer, ratedUser, meet);

        return "성공적으로 반영되었습니다.";
    }
}