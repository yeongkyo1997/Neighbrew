package com.ssafy.backend.domain.chat.service.room.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.entity.ChatRoomUser;
import com.ssafy.backend.domain.chat.repository.ChatRoomRepository;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomCreateRequest;
import com.ssafy.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomManager {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public List<ChatRoom> findUserChatRooms(Long userId) {
        return chatRoomUserRepository.findByUserUserId(userId).stream().map(ChatRoomUser::getChatRoom).collect(
                Collectors.toList());
    }

    public ChatRoom createChatRoom(ChatRoomCreateRequest request) {
        return chatRoomRepository.save(ChatRoom.builder().chatRoomName(request.name).build());
    }

    public ChatRoom findChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방을 찾을 수 없습니다. id=" + roomId));
    }

    public ChatRoom getChatRoomDetail(Long chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new NotFoundException("채팅방을 찾을 수 없습니다. id=" + chatRoomId));
    }
}