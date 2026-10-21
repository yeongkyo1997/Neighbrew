package com.ssafy.backend.authentication.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.backend.authentication.domain.oauth.OAuthInfoResponse;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service("authUserService")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Long findOrCreateUser(OAuthInfoResponse oAuthInfo) {
        return findByEmail(oAuthInfo.getEmail()).map(User::getUserId).orElseGet(() -> createNewUser(oAuthInfo));
    }

    @Transactional
    public Long createNewUser(OAuthInfoResponse oAuthInfo) {
        User user = buildUser(oAuthInfo);
        return userRepository.save(user).getUserId();
    }

    private User buildUser(OAuthInfoResponse oAuthInfo) {
        if (oAuthInfo.getEmail() == null) {
            return buildUserWithoutEmail(oAuthInfo);
        }
        return buildUserWithEmail(oAuthInfo);
    }

    private User buildUserWithEmail(OAuthInfoResponse oAuthInfo) {
        return User.builder().email(oAuthInfo.getEmail()).name(oAuthInfo.getName()).nickname(
                oAuthInfo.getNickname()).oAuthProvider(oAuthInfo.getOAuthProvider()).build();
    }

    private User buildUserWithoutEmail(OAuthInfoResponse oAuthInfo) {
        String uniqueSuffix = generateUniqueSuffix();
        return User.builder().email(oAuthInfo.getName()).name(oAuthInfo.getName()).nickname(
                oAuthInfo.getName() + uniqueSuffix).oAuthProvider(oAuthInfo.getOAuthProvider()).build();
    }

    private String generateUniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 7);
    }
}