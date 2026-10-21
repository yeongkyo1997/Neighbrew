package com.ssafy.backend.domain.evaluation.service.evaluation.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.repository.EvaluationRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EvaluationManager {
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;

    // Evaluation 엔티티는 ratedUser 컬럼에 평가자, reviewer 컬럼에 평가 대상을 저장한다
    public void saveEvaluation(EvaluationRequestDto evaluationRequestDto, User target, User evaluator,
                               Meet meet) {
        userRepository.save(target);
        evaluationRepository.save(evaluationRequestDto.toEntity(target, evaluator, meet));
    }
}