package com.ssafy.backend.authentication.application;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.authentication.domain.oauth.OAuthLoginParams;
import com.ssafy.backend.authentication.domain.oauth.RequestOAuthInfoService;
import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.OAuthProviderException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private static final int NAME_MAX_LENGTH = 20;

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
        // 고유 ID가 없으면 서로 다른 사용자를 구분할 수 없으므로 가입을 막는다 (provider 응답 문제이므로 502)
        if (oAuthId == null) {
            throw new OAuthProviderException("OAuth 사용자 ID를 받지 못했습니다");
        }
        return userRepository.findByOAuth(oAuthInfoResponse.getOAuthProvider(), oAuthId).map(User::getUserId)
                             .orElseGet(() -> newUser(oAuthInfoResponse, oAuthId));
    }

    // login에 @Transactional을 걸지 않는다. 걸면 유니크 제약 위반이 커밋 시점에 나고 영속성 컨텍스트가 오염되어 다시 조회할 수 없다.
    private Long newUser(OAuthInfoResponse oAuthInfoResponse, String oAuthId) {
        String nickname = generateNickname();
        User user = User.builder().email(oAuthInfoResponse.getEmail()).name(safeName(oAuthInfoResponse.getName(), nickname))
                        .nickname(nickname).oAuthProvider(oAuthInfoResponse.getOAuthProvider()).oAuthId(oAuthId)
                        .build();
        try {
            return userRepository.save(user).getUserId();
        } catch (DataIntegrityViolationException e) {
            // 같은 사용자의 첫 로그인 요청이 동시에 들어오면 한쪽이 먼저 가입한다. 그 계정으로 로그인시킨다.
            return userRepository.findByOAuth(oAuthInfoResponse.getOAuthProvider(), oAuthId).map(User::getUserId)
                                 .orElseThrow(() -> e);
        }
    }

    // User.name은 not null, 20자 제한이다. 이름 제공에 동의하지 않았으면 닉네임을 쓰고, 길면 자른다.
    // (이모지 같은 서로게이트 쌍이 중간에서 잘리지 않도록 코드포인트 기준으로 자른다)
    private String safeName(String name, String nickname) {
        if (name == null || name.isBlank()) {
            return nickname;
        }
        String trimmed = name.strip();
        if (trimmed.codePointCount(0, trimmed.length()) <= NAME_MAX_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, trimmed.offsetByCodePoints(0, NAME_MAX_LENGTH));
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