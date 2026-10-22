package com.ssafy.backend.domain.meet.service.meet.event;

import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetAccepted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetDeleted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageDiscarded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageUploaded;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetImageManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetPushManager;
import com.ssafy.backend.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class MeetEventListenerTest {
    @Mock
    private MeetPushManager pushManager;
    @Mock
    private MeetImageManager imageManager;

    @InjectMocks
    private MeetEventListener listener;

    private final User host = user(1L, "host");
    private final User guest = user(2L, "guest");

    @Test
    void 모임_삭제가_커밋되면_참여자에게_알린다() {
        Meet meet = Meet.builder().meetId(10L).host(host).build();

        listener.onMeetDeleted(new MeetDeleted(meet, List.of()));

        then(pushManager).should().notifyMeetDeletion(meet, List.of());
    }

    @Test
    void 승인이_커밋되면_신청자에게_알린다() {
        listener.onMeetAccepted(new MeetAccepted(host, guest, "모임", 10L));

        then(pushManager).should().notifyMeetAcceptance(host, guest, "모임", 10L);
    }

    @Test
    void 커밋되면_더_이상_쓰지_않는_이미지를_지운다() {
        listener.onMeetImageDiscarded(new MeetImageDiscarded("old.png"));

        then(imageManager).should().deleteImageIfNotDefault("old.png");
    }

    @Test
    void 롤백되면_방금_올린_이미지를_지운다() {
        listener.onMeetImageUploadRolledBack(new MeetImageUploaded("new.png"));

        then(imageManager).should().deleteImageIfNotDefault("new.png");
    }

    @Test
    void 커밋_후_알림이_실패해도_예외를_밖으로_던지지_않는다() {
        willThrow(new IllegalStateException("SSE 전송 실패")).given(pushManager)
                                                         .notifyMeetAcceptance(host, guest, "모임", 10L);

        assertThatCode(() -> listener.onMeetAccepted(new MeetAccepted(host, guest, "모임", 10L)))
                .doesNotThrowAnyException();
    }
}
