package com.ssafy.backend.domain.evaluation.service.evaluation.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.User;

@Component
public class EvaluationScoreManager {
    private static final String ERR_INVALID_EVAL_TYPE = "평가는 'good' 또는 'bad' 또는 'mid'로 입력해야 합니다.";

    public void updateReviewerScore(User reviewer, String evaluationType, Long totalUsers) {
        float score;
        switch (evaluationType.toLowerCase()) {
            case "good":
                score = 1.0f / totalUsers;
                reviewer.updateLiverPoint(reviewer.getLiverPoint() + score);
                break;
            case "bad":
                score = 3.0f / totalUsers;
                reviewer.updateLiverPoint(reviewer.getLiverPoint() - score);
                break;
            case "mid":
                break;
            default:
                throw new IllegalArgumentException(ERR_INVALID_EVAL_TYPE);
        }
    }
}