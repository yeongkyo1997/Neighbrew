package com.ssafy.backend.authentication.application;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.RequestOAuthInfoService;
import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    /*
    카카오/네이버와 같은 OAuth 플랫폼에 인증 후 프로필 정보 가져오기
    provider와 고유 ID로 사용자 확인 (없으면 새로 가입처리)
    Access Token 생성 후 내려주기
     */
    private final UserRepository userRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public LoginResponse login(OAuthLoginParams params)
            throws JsonProcessingException, UnsupportedEncodingException {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);

        // 받아온 정보를 기반으로  userId를 추출
        Long userId = findOrCreateUser(oAuthInfoResponse);
        // 그 아이디를 기반으로 token 생성

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(JwtUtil.generateToken(String.valueOf(userId)));
        loginResponse.setRefreshToken(JwtUtil.generateRefreshToken(String.valueOf(userId)));
        loginResponse.setUserId(userId);

        return loginResponse;
    }

    // provider와 provider 안의 고유 ID로 user의 id를 반환
    // 이메일은 동의 여부에 따라 바뀌거나 없을 수 있고, 다른 provider에서 인증 없이 같은 값을 쓸 수 있으므로 조회에 쓰지 않는다.
    private Long findOrCreateUser(OAuthInfoResponse oAuthInfoResponse) {
        String oAuthId = oAuthInfoResponse.getOAuthId();
        // 고유 ID가 없으면 서로 다른 사용자를 구분할 수 없으므로 가입을 막는다
        if (oAuthId == null) {
            throw new IllegalArgumentException("OAuth 사용자 ID를 받지 못했습니다");
        }
        return userRepository.findByOAuth(oAuthInfoResponse.getOAuthProvider(), oAuthId).map(User::getUserId)
                             .orElseGet(() -> newUser(oAuthInfoResponse, oAuthId));
    }

    private Long newUser(OAuthInfoResponse oAuthInfoResponse, String oAuthId) {
        User user = User.builder().email(oAuthInfoResponse.getEmail()).name(oAuthInfoResponse.getName())
                        .nickname(generateNickname()).oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                        .oAuthId(oAuthId).build();
        return userRepository.save(user).getUserId();
    }

    // 닉네임은 공개되므로 이메일·실명 대신 겹치지 않는 무작위 값으로 만든다. (가입 후 마이페이지에서 변경)
    private String generateNickname() {
        String nickname;
        do {
            nickname = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }

    public String redirectApiUrl(OAuthLoginParams params) {
        return requestOAuthInfoService.authApiUrl(params);
    }
}