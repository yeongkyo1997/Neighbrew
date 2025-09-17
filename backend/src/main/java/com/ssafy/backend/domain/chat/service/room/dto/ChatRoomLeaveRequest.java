package com.ssafy.backend.domain.chat.service.room.dto;

public class ChatRoomLeaveRequest {
    public final Long userId;

    public ChatRoomLeaveRequest(Long userId) {
        this.userId = userId;
    }
}