package com.ssafy.backend.domain.push.service.push.manager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.push.entity.Push;
import com.ssafy.backend.domain.push.repository.PushRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class PushDataManagerTest {
    @Mock
    private PushRepository pushRepository;

    @InjectMocks
    private PushDataManager pushDataManager;

    private Push pushReceivedBy(Long receiverId) {
        return Push.builder().pushId(10L).receiver(User.builder().userId(receiverId).build()).build();
    }

    @Test
    void 남이_받은_알림은_삭제할_수_없다() {
        given(pushRepository.findById(10L)).willReturn(Optional.of(pushReceivedBy(1L)));

        assertThatThrownBy(() -> pushDataManager.deletePushById(10L, 2L)).isInstanceOf(ForbiddenException.class);
        then(pushRepository).should(never()).delete(any());
    }

    @Test
    void 본인이_받은_알림은_삭제한다() {
        Push push = pushReceivedBy(1L);
        given(pushRepository.findById(10L)).willReturn(Optional.of(push));

        pushDataManager.deletePushById(10L, 1L);

        then(pushRepository).should().delete(push);
    }

    @Test
    void 없는_알림은_무시한다() {
        given(pushRepository.findById(10L)).willReturn(Optional.empty());

        pushDataManager.deletePushById(10L, 1L);

        then(pushRepository).should(never()).delete(any());
    }
}
