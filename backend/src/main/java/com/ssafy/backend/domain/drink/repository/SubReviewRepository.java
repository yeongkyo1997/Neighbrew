package com.ssafy.backend.domain.drink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.drink.entity.SubReview;

public interface SubReviewRepository extends JpaRepository<SubReview, Long> {
    List<SubReview> findByDrinkReviewDrinkReviewIdOrderByCreatedAtDesc(Long drinkReviewId);
}
