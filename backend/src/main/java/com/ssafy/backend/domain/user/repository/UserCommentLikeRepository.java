package com.ssafy.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.user.entity.UserCommentLike;

@Repository
public interface UserCommentLikeRepository extends JpaRepository<UserCommentLike, Long> {
    Optional<UserCommentLike> findByUserUserIdAndDrinkReviewDrinkReviewId(Long userId, Long drinkReviewId);
}
