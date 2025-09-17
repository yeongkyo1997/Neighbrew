package com.ssafy.backend.domain.push.service.push.util;

import org.springframework.stereotype.Component;

@Component
public class PushIdGenerator {

    public String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }
}