package com.ssafy.backend.domain.meet.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.ssafy.backend.domain.user.entity.User;

class MeetTest {
    private Meet meet(int now, int max) {
        return Meet.builder().nowParticipants(now).maxParticipants(max).build();
    }

    @Test
    void 정원보다_적으면_참여_인원이_늘어난다() {
        Meet meet = meet(7, 8);

        meet.addParticipant();

        assertThat(meet.getNowParticipants()).isEqualTo(8);
    }

    @Test
    void 정원이_가득_차면_참여_인원을_늘리지_않는다() {
        Meet meet = meet(8, 8);

        assertThatThrownBy(meet::addParticipant).isInstanceOf(IllegalArgumentException.class);
        assertThat(meet.getNowParticipants()).isEqualTo(8);
    }

    @Test
    void 참여_인원이_줄어든다() {
        Meet meet = meet(3, 8);

        meet.removeParticipant();

        assertThat(meet.getNowParticipants()).isEqualTo(2);
    }

    @Test
    void 방장만_남은_모임은_인원을_줄이지_않는다() {
        Meet meet = meet(1, 8);

        assertThatThrownBy(meet::removeParticipant).isInstanceOf(IllegalStateException.class);
        assertThat(meet.getNowParticipants()).isEqualTo(1);
    }

    @Test
    void 생성할_때는_보낸_값과_관계없이_방장_1명으로_시작한다() {
        Meet meet = meet(8, 8);

        meet.prePersist();

        assertThat(meet.getNowParticipants()).isEqualTo(1);
    }

    @Test
    void 최대_인원을_현재_인원보다_적게_바꿀_수_없다() {
        Meet meet = meet(3, 8);

        assertThatThrownBy(() -> meet.update(meet(1, 2))).isInstanceOf(IllegalArgumentException.class);
        assertThat(meet.getMaxParticipants()).isEqualTo(8);
    }

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 22);

    private Meet limitedMeet(Integer minAge, Integer maxAge, Float minLiverPoint) {
        return Meet.builder().nowParticipants(1).maxParticipants(8).minAge(minAge).maxAge(maxAge)
                   .minLiverPoint(minLiverPoint).build();
    }

    // TODAY 기준으로 (올해 - 출생 연도 + 1) = age가 되는 사용자
    private User userAged(Integer age, float liverPoint) {
        LocalDate birth = age == null ? null : LocalDate.of(TODAY.getYear() - age + 1, 6, 1);
        return User.builder().userId(9L).birth(birth).liverPoint(liverPoint).build();
    }

    @Test
    void 간수치가_제한보다_낮으면_참여할_수_없다() {
        Meet meet = limitedMeet(null, null, 50f);

        assertThatThrownBy(() -> meet.validateEligibility(userAged(30, 40f), TODAY))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("간수치");
    }

    @Test
    void 나이가_최소보다_적거나_최대보다_많으면_참여할_수_없다() {
        Meet meet = limitedMeet(25, 30, null);

        assertThatThrownBy(() -> meet.validateEligibility(userAged(24, 40f), TODAY))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("나이");
        assertThatThrownBy(() -> meet.validateEligibility(userAged(31, 40f), TODAY))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("나이");
    }

    @Test
    void 나이가_경계값과_같으면_참여할_수_있다() {
        Meet meet = limitedMeet(25, 30, 40f);

        assertThatCode(() -> meet.validateEligibility(userAged(25, 40f), TODAY)).doesNotThrowAnyException();
        assertThatCode(() -> meet.validateEligibility(userAged(30, 40f), TODAY)).doesNotThrowAnyException();
    }

    @Test
    void 나이_제한이_있는데_생년월일이_없으면_참여할_수_없다() {
        Meet meet = limitedMeet(25, null, null);

        assertThatThrownBy(() -> meet.validateEligibility(userAged(null, 40f), TODAY))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("생년월일");
    }

    @Test
    void 나이_제한이_없으면_생년월일이_없어도_참여할_수_있다() {
        assertThatCode(() -> limitedMeet(null, 0, 40f).validateEligibility(userAged(null, 40f), TODAY))
                .doesNotThrowAnyException();
        assertThatCode(() -> limitedMeet(null, null, null).validateEligibility(userAged(null, 0f), TODAY))
                .doesNotThrowAnyException();
    }
}
