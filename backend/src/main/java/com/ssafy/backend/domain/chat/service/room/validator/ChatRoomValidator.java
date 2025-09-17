package com.ssafy.backend.domain.chat.service.room.validator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomCreateRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomJoinRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomLeaveRequest;
import com.ssafy.backend.domain.chat.service.room.dto.ChatRoomMessageRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomValidator {
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatRoomCreateRequest parseCreateRequest(Map<String, Object> map) {
        String name = (String) map.get("name");
        List<Integer> userIdList = (List<Integer>) map.get("userIdList");
        return new ChatRoomCreateRequest(name, userIdList);
    }

    public ChatRoomMessageRequest parseMessageRequest(String payload) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(payload);
        Long userId = jsonNode.get("userId").asLong();
        String message = jsonNode.get("message").asText();
        return new ChatRoomMessageRequest(userId, message);
    }

    public ChatRoomJoinRequest parseJoinRequest(String payload) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(payload);
        Long userId = jsonNode.get("userId").asLong();
        return new ChatRoomJoinRequest(userId);
    }

    public ChatRoomLeaveRequest parseLeaveRequest(String payload) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(payload);
        Long userId = jsonNode.get("userId").asLong();
        return new ChatRoomLeaveRequest(userId);
    }
}