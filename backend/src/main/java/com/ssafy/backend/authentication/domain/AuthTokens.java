package com.ssafy.backend.authentication.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String grantType = "Bearer";
    @Builder.Default
    private Long expiresIn = 3600L;
}

