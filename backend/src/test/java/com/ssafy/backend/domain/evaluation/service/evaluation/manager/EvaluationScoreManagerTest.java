package com.ssafy.backend.domain.evaluation.service.evaluation.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.ssafy.backend.domain.user.entity.User;

class EvaluationScoreManagerTest {
    private final EvaluationScoreManager scoreManager = new EvaluationScoreManager();

    @Test
    void 좋은_평가는_인원수로_나눈_만큼_점수를_올린다() {
        User target = User.builder().liverPoint(40.0f).build();

        scoreManager.updateTargetScore(target, "GOOD", 2L);

        assertThat(target.getLiverPoint()).isEqualTo(40.5f);
    }

    @Test
    void 나쁜_평가는_인원수로_나눈_만큼_점수를_내린다() {
        User target = User.builder().liverPoint(40.0f).build();

        scoreManager.updateTargetScore(target, "BAD", 2L);

        assertThat(target.getLiverPoint()).isEqualTo(38.5f);
    }

    @Test
    void 점수는_100을_넘지_않는다() {
        User target = User.builder().liverPoint(99.9f).build();

        scoreManager.updateTargetScore(target, "GOOD", 2L);

        assertThat(target.getLiverPoint()).isEqualTo(100.0f);
    }

    @Test
    void 점수는_0_아래로_내려가지_않는다() {
        User target = User.builder().liverPoint(1.0f).build();

        scoreManager.updateTargetScore(target, "BAD", 2L);

        assertThat(target.getLiverPoint()).isEqualTo(0.0f);
    }

    @Test
    void 보통_평가는_점수를_바꾸지_않는다() {
        User target = User.builder().liverPoint(40.0f).build();

        scoreManager.updateTargetScore(target, "MID", 2L);

        assertThat(target.getLiverPoint()).isEqualTo(40.0f);
    }

    @Test
    void 알_수_없는_평가_유형은_거부한다() {
        User target = User.builder().liverPoint(40.0f).build();

        assertThatThrownBy(() -> scoreManager.updateTargetScore(target, "GREAT", 2L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(target.getLiverPoint()).isEqualTo(40.0f);
    }
}
