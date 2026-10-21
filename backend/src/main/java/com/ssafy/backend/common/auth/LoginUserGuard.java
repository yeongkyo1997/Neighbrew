package com.ssafy.backend.common.auth;

import com.ssafy.backend.exception.ForbiddenException;

public final class LoginUserGuard {
    private LoginUserGuard() {
    }

    // 경로의 userId가 로그인 사용자와 다르면 403으로 거부한다.
    public static void requireSame(Long pathUserId, Long loginUserId) {
        if (!loginUserId.equals(pathUserId)) {
            throw new ForbiddenException("본인 정보만 요청할 수 있습니다.");
        }
    }
}
