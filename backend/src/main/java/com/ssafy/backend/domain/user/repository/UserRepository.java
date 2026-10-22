package com.ssafy.backend.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);

    @Query("select u from User u where u.oAuthProvider = :provider and u.oAuthId = :oAuthId")
    Optional<User> findByOAuth(@Param("provider") OAuthProvider provider, @Param("oAuthId") String oAuthId);

    // 이메일이 닉네임으로 남은 예전 가입자는 공개 닉네임(user_{id})과 다르므로 excluded("@")로 검색 대상에서 뺀다.
    List<User> findByNicknameContainingAndNicknameNotContaining(String nickname, String excluded);

    boolean existsByNickname(String name);
}
