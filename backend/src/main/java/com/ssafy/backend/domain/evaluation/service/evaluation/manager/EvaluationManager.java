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

    public void saveEvaluation(EvaluationRequestDto evaluationRequestDto, User reviewer, User ratedUser,
                               Meet meet) {
        userRepository.save(reviewer);
        evaluationRepository.save(evaluationRequestDto.toEntity(reviewer, ratedUser, meet));
    }
}