package com.ssafy.backend.domain.chat.service.room.manager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class ChatRoomUserManagerTest {
    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MeetUserRepository meetUserRepository;

    @InjectMocks
    private ChatRoomUserManager chatRoomUserManager;

    @Test
    void 채팅방과_모임_모두_참여하지_않으면_거부한다() {
        given(chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(1L, 2L)).willReturn(false);
        given(meetUserRepository.findStatusByUserIdAndChatRoomId(2L, 1L)).willReturn(null);

        assertThatThrownBy(() -> chatRoomUserManager.requireRoomMember(1L, 2L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 모임_신청만_한_사용자는_거부한다() {
        given(chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(1L, 2L)).willReturn(false);
        given(meetUserRepository.findStatusByUserIdAndChatRoomId(2L, 1L)).willReturn(Status.APPLY);

        assertThatThrownBy(() -> chatRoomUserManager.requireRoomMember(1L, 2L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 채팅방_참여자는_허용한다() {
        given(chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(1L, 2L)).willReturn(true);

        assertThatCode(() -> chatRoomUserManager.requireRoomMember(1L, 2L)).doesNotThrowAnyException();
    }

    @Test
    void 채팅방_재입장_전인_모임_참여자는_허용한다() {
        given(chatRoomUserRepository.existsByChatRoomChatRoomIdAndUserUserId(1L, 2L)).willReturn(false);
        given(meetUserRepository.findStatusByUserIdAndChatRoomId(2L, 1L)).willReturn(Status.GUEST);

        assertThatCode(() -> chatRoomUserManager.requireRoomMember(1L, 2L)).doesNotThrowAnyException();
    }
}
