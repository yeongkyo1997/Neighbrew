package com.ssafy.backend.domain.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.follow.entity.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    List<Follow> findByFollowingUserId(Long userId);

    List<Follow> findByFollowerUserId(Long userId);
}
