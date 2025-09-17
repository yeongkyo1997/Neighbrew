package com.ssafy.backend.domain.chat.service.validator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.domain.chat.service.dto.ChatLeaveRequest;
import com.ssafy.backend.domain.chat.service.dto.ChatMessageRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatDmValidator {
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatMessageRequest parseMessageRequest(String payload) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(payload);
        String message = jsonNode.get("message").asText();
        Long senderId = jsonNode.get("senderId").asLong();
        return new ChatMessageRequest(message, senderId);
    }

    public ChatLeaveRequest parseLeaveRequest(String payload) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(payload);
        Long leaveUserId = jsonNode.get("leaveUserId").asLong();
        return new ChatLeaveRequest(leaveUserId);
    }
}