package com.ssafy.backend.domain.push.service.push;

import static com.ssafy.backend.support.UserFixture.user;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.service.push.manager.PushDataManager;
import com.ssafy.backend.domain.push.service.push.manager.SseConnectionManager;
import com.ssafy.backend.domain.push.service.push.manager.SseEventManager;
import com.ssafy.backend.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class PushServiceTest {
    @Mock
    private SseConnectionManager connectionManager;
    @Mock
    private SseEventManager eventManager;
    @Mock
    private PushDataManager dataManager;

    @InjectMocks
    private PushService pushService;

    private final User host = user(1L, "host");
    private final User guest = user(2L, "guest");

    private void givenSavedPush() {
        given(dataManager.createAndSavePush(any(), any(), any(), any(), any())).willAnswer(
                invocation -> Push.builder().pushId(1L).sender(invocation.getArgument(0))
                                  .receiver(invocation.getArgument(1)).pushType(invocation.getArgument(2))
                                  .content(invocation.getArgument(3)).url(invocation.getArgument(4)).build());
    }

    @ParameterizedTest
    @ValueSource(strings = { "https://neighbrew.com", "https://neighbrew.com/" })
    void 도메인은_PushService에서_한_번만_붙인다(String neighbrewUrl) {
        ReflectionTestUtils.setField(pushService, "neighbrewUrl", neighbrewUrl);
        givenSavedPush();

        pushService.send(host, guest, PushMessage.meetAccepted("모임", 12L));

        then(dataManager).should().createAndSavePush(host, guest, PushType.MEETACCESS,
                                                     "회원님께서 모임(모임)참여 되셨습니다.\n 즐거운 시간 되세요.",
                                                     "https://neighbrew.com/meet/12");
    }

    @Test
    void 저장한_푸시를_받는_사람에게_보낸다() {
        ReflectionTestUtils.setField(pushService, "neighbrewUrl", "https://neighbrew.com");
        givenSavedPush();

        pushService.send(host, guest, PushMessage.meetRejected("모임"));

        then(eventManager).should().broadcastToUser(any(), any());
        then(dataManager).should().createAndSavePush(host, guest, PushType.MEETREJECT,
                                                     "회원님께서 모임(모임)참여에 거절당했습니다.", "https://neighbrew.com/meet");
    }
}
