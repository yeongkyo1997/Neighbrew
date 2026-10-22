package com.ssafy.backend.domain.push.service.push.manager;

import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.repository.EmitterRepository;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class SseEventManagerTest {
    @Mock
    private EmitterRepository emitterRepository;
    @Mock
    private PushRepository pushRepository;

    @InjectMocks
    private SseEventManager eventManager;

    private final User sender = user(1L, "보낸사람");
    private final User receiver = user(2L, "받는사람");

    private Push push(long pushId) {
        return Push.builder().pushId(pushId).sender(sender).receiver(receiver).pushType(PushType.FOLLOW)
                   .content("알림" + pushId).url("https://neighbrew.com/mypage/1").build();
    }

    // SSE 이벤트의 텍스트 부분(id:, data: 등)만 이어 붙인다
    private static String text(SseEmitter.SseEventBuilder event) {
        return event.build().stream().map(part -> String.valueOf(part.getData())).collect(Collectors.joining());
    }

    private List<String> sentEvents(SseEmitter emitter, int count) throws Exception {
        ArgumentCaptor<SseEmitter.SseEventBuilder> captor = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
        then(emitter).should(times(count)).send(captor.capture());
        return captor.getAllValues().stream().map(SseEventManagerTest::text).toList();
    }

    @Test
    void 알림의_이벤트_ID는_pushId다() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        given(emitterRepository.findAllEmitterStartWithByUserId("2")).willReturn(Map.of("2_100", emitter));

        eventManager.broadcastToUser(2L, push(15L).toDto());

        assertThat(sentEvents(emitter, 1).get(0)).contains("id:15\n");
    }

    @Test
    void 연결_확인_이벤트에는_ID를_붙이지_않는다() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);

        eventManager.sendInitialEvent(emitter, 2L, "2_100");

        assertThat(sentEvents(emitter, 1).get(0)).doesNotContain("id:").contains("EventStream Created");
    }

    @Test
    void 재연결하면_마지막으로_받은_알림_이후의_알림을_순서대로_다시_보낸다() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        given(pushRepository.findTop50ByReceiver_UserIdAndPushIdGreaterThanOrderByPushIdAsc(2L, 10L)).willReturn(
                List.of(push(11L), push(12L)));

        eventManager.sendLostData("10", 2L, "2_200", emitter);

        List<String> events = sentEvents(emitter, 2);
        assertThat(events.get(0)).contains("id:11\n");
        assertThat(events.get(1)).contains("id:12\n");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "2_1700000000000", "abc" })
    void 마지막_ID가_없거나_예전_형식이면_다시_보내지_않는다(String lastEventId) throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);

        eventManager.sendLostData(lastEventId, 2L, "2_200", emitter);

        then(pushRepository).should(never()).findTop50ByReceiver_UserIdAndPushIdGreaterThanOrderByPushIdAsc(anyLong(),
                                                                                                             anyLong());
        then(emitter).should(never()).send(any(SseEmitter.SseEventBuilder.class));
    }
}
