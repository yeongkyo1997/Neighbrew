package com.ssafy.backend.domain.chat.service.room.manager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.chat.repository.ChatRoomRepository;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class ChatRoomManagerTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    @InjectMocks
    private ChatRoomManager chatRoomManager;

    @Test
    void 없는_채팅방은_404로_응답할_예외를_던진다() {
        given(chatRoomRepository.findById(7L)).willReturn(Optional.empty());
        given(chatRoomRepository.findByChatRoomId(7L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomManager.findChatRoomById(7L)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> chatRoomManager.getChatRoomDetail(7L)).isInstanceOf(NotFoundException.class);
    }
}
