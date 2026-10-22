package com.ssafy.backend.domain.meet.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

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
}
