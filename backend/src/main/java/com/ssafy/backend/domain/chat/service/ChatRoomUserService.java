package com.ssafy.backend.domain.chat.service;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.chat.entity.ChatRoomUser;
import com.ssafy.backend.domain.chat.repository.ChatRoomUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomUserService {
    private final ChatRoomUserRepository chatRoomUserRepository;

    public ChatRoomUser save(ChatRoomUser chatRoomUser) {
        return chatRoomUserRepository.save(chatRoomUser);
    }
}
