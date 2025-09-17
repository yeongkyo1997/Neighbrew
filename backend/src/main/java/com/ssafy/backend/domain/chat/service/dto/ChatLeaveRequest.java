package com.ssafy.backend.domain.chat.service.dto;

public class ChatLeaveRequest {
    public final Long leaveUserId;

    public ChatLeaveRequest(Long leaveUserId) {
        this.leaveUserId = leaveUserId;
    }
}