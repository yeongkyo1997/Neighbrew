package com.ssafy.backend.domain.evaluation.dto;

import com.ssafy.backend.domain.evaluation.entity.Evaluation;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.dto.UserResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDto {
    public static EvaluationResponseDto fromEntity(Evaluation evaluation) {
        return EvaluationResponseDto.builder()
                                    .evaluationId(evaluation.getEvaluationId())
                                    .ratedUser(UserResponseDto.fromEntity(evaluation.getRatedUser()))
                                    .reviewer(UserResponseDto.fromEntity(evaluation.getReviewer()))
                                    .meet(evaluation.getMeet())
                                    .evaluationType(evaluation.getEvaluationType().toString())
                                    .description(evaluation.getDescription())
                                    .build();
    }

    private Long evaluationId;
    private UserResponseDto ratedUser;
    private UserResponseDto reviewer;
    private Meet meet;
    private String evaluationType;
    private String description;
}
