package com.ssafy.backend.domain.evaluation.service.evaluation.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.user.entity.User;

@Component
public class EvaluationScoreManager {
    private static final String ERR_INVALID_EVAL_TYPE = "평가는 'good' 또는 'bad' 또는 'mid'로 입력해야 합니다.";
    private static final float MIN_LIVER_POINT = 0.0f;
    private static final float MAX_LIVER_POINT = 100.0f;

    public void updateTargetScore(User target, String evaluationType, Long totalUsers) {
        switch (evaluationType.toLowerCase()) {
            case "good":
                applyDelta(target, 1.0f / totalUsers);
                break;
            case "bad":
                applyDelta(target, -3.0f / totalUsers);
                break;
            case "mid":
                break;
            default:
                throw new IllegalArgumentException(ERR_INVALID_EVAL_TYPE);
        }
    }

    // liverPoint는 0 이상 100 이하로 유지한다
    private void applyDelta(User target, float delta) {
        float updated = target.getLiverPoint() + delta;
        target.updateLiverPoint(Math.max(MIN_LIVER_POINT, Math.min(MAX_LIVER_POINT, updated)));
    }
}
