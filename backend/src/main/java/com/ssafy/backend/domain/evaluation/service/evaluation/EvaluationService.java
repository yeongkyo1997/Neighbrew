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

    // 요청 본문의 reviewer 필드는 평가 대상의 ID다 (프론트 호환을 위해 필드명 유지)
    @Transactional
    public String calculateScoreByMeetId(EvaluationRequestDto evaluationRequestDto, Long userId) {
        Long targetId = evaluationRequestDto.getReviewer();
        Long meetId = evaluationRequestDto.getMeetId();

        validator.validateSelfEvaluation(evaluationRequestDto, userId);
        validator.validateParticipants(meetId, userId, targetId);

        User evaluator = validator.validateAndGetEvaluator(userId);
        User target = validator.validateAndGetTarget(targetId);
        Meet meet = validator.validateAndGetMeet(meetId);

        validator.validateMeetEnded(meet);
        validator.validateNotAlreadyEvaluated(evaluator, target, meet);

        Long totalUsers = validator.validateAndGetTotalUsers(meetId);

        scoreManager.updateTargetScore(target, evaluationRequestDto.getEvaluationType(), totalUsers);
        evaluationManager.saveEvaluation(evaluationRequestDto, target, evaluator, meet);

        return "성공적으로 반영되었습니다.";
    }
}
