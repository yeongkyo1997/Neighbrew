package com.ssafy.backend.domain.push.service.push;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ssafy.backend.common.enums.PushType;

class PushMessageTest {
    @Test
    void 모든_푸시_경로는_서비스_안의_절대_경로다() {
        List<PushMessage> messages = List.of(PushMessage.reviewLike("a", 1L, 2L), PushMessage.subReview("a", 1L, 2L),
                                             PushMessage.follow("a", 1L), PushMessage.directMessage("a", 1L, 2L),
                                             PushMessage.meetCreated("a", "모임", 1L),
                                             PushMessage.meetModified("모임", 1L), PushMessage.meetDeleted("a", "모임"),
                                             PushMessage.meetApplied("a", "모임", 1L),
                                             PushMessage.meetAccepted("모임", 1L), PushMessage.meetRejected("모임"),
                                             PushMessage.meetEvaluation("모임", 1L));

        assertThat(messages).allSatisfy(message -> assertThat(message.path()).startsWith("/"));
    }

    @Test
    void 승인과_거절_알림도_다른_알림과_같은_경로_형식을_쓴다() {
        assertThat(PushMessage.meetAccepted("모임", 12L).path()).isEqualTo("/meet/12");
        assertThat(PushMessage.meetRejected("모임").path()).isEqualTo("/meet");
    }

    @Test
    void 신청과_승인_알림은_신청_취소_시_지울_수_있도록_MEETACCESS_유형을_쓴다() {
        assertThat(PushMessage.meetApplied("a", "모임", 1L).pushType()).isEqualTo(PushType.MEETACCESS);
        assertThat(PushMessage.meetAccepted("모임", 1L).pushType()).isEqualTo(PushType.MEETACCESS);
    }

    @Test
    void 좋아요_알림은_후기_상세_경로로_이동한다() {
        PushMessage message = PushMessage.reviewLike("홍길동", 3L, 7L);

        assertThat(message.pushType()).isEqualTo(PushType.REVIEWLIKE);
        assertThat(message.path()).isEqualTo("/drinkpost/3/7");
        assertThat(message.content()).startsWith("홍길동님이");
    }

    @Test
    void 상대_경로는_만들_수_없다() {
        assertThatThrownBy(() -> new PushMessage(PushType.MEETREJECT, "내용", "meet"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
