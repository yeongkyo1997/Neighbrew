package com.ssafy.backend.domain.chat.service.room.dto;

public class ChatRoomJoinRequest {
    public final Long userId;

    public ChatRoomJoinRequest(Long userId) {
        this.userId = userId;
    }
}