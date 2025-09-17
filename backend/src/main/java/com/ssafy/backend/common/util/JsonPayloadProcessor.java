package com.ssafy.backend.common.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JsonPayloadProcessor {
    private final ObjectMapper objectMapper;

    public ChatMessagePayload parseChatMessage(String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        return ChatMessagePayload.builder()
                                 .message(jsonNode.get("message").asText())
                                 .senderId(jsonNode.get("senderId").asLong())
                                 .build();
    }

    public LeaveRoomPayload parseLeaveRoom(String payload) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        return LeaveRoomPayload.builder()
                               .leaveUserId(jsonNode.get("leaveUserId").asLong())
                               .build();
    }

    public static class ChatMessagePayload {
        public static ChatMessagePayloadBuilder builder() {
            return new ChatMessagePayloadBuilder();
        }

        private final String message;
        private final Long senderId;

        private ChatMessagePayload(String message, Long senderId) {
            this.message = message;
            this.senderId = senderId;
        }

        public String getMessage() {
            return message;
        }

        public Long getSenderId() {
            return senderId;
        }

        public static class ChatMessagePayloadBuilder {
            private String message;
            private Long senderId;

            public ChatMessagePayloadBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ChatMessagePayloadBuilder senderId(Long senderId) {
                this.senderId = senderId;
                return this;
            }

            public ChatMessagePayload build() {
                return new ChatMessagePayload(message, senderId);
            }
        }
    }

    public static class LeaveRoomPayload {
        public static LeaveRoomPayloadBuilder builder() {
            return new LeaveRoomPayloadBuilder();
        }

        private final Long leaveUserId;

        private LeaveRoomPayload(Long leaveUserId) {
            this.leaveUserId = leaveUserId;
        }

        public Long getLeaveUserId() {
            return leaveUserId;
        }

        public static class LeaveRoomPayloadBuilder {
            private Long leaveUserId;

            public LeaveRoomPayloadBuilder leaveUserId(Long leaveUserId) {
                this.leaveUserId = leaveUserId;
                return this;
            }

            public LeaveRoomPayload build() {
                return new LeaveRoomPayload(leaveUserId);
            }
        }
    }
}