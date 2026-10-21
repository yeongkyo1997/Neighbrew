package com.ssafy.backend.common.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

// SSE(EventSource)는 헤더를 보낼 수 없으므로, access 토큰 대신 URL에 실을 단기·일회용 티켓을 발급한다.
@Component
public class SseTicketStore {
    private static final long TTL_MILLIS = 30_000L;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public String issue(String userId) {
        long now = System.currentTimeMillis();
        tickets.values().removeIf(ticket -> ticket.expiresAt < now); // 만료된 티켓 정리

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tickets.put(value, new Ticket(userId, now + TTL_MILLIS));
        return value;
    }

    // 티켓을 소비(1회용)하고 유효하면 userId를, 아니면 null을 반환한다.
    public String consume(String value) {
        if (value == null) {
            return null;
        }
        Ticket ticket = tickets.remove(value);
        if (ticket == null || ticket.expiresAt < System.currentTimeMillis()) {
            return null;
        }
        return ticket.userId;
    }

    private static class Ticket {
        private final String userId;
        private final long expiresAt;

        private Ticket(String userId, long expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }
}
