package com.ssafy.backend.domain.drink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.user.entity.User;

@Repository
public interface DrinkReviewRepository extends JpaRepository<DrinkReview, Long> {
    @EntityGraph(attributePaths = {"user", "drink"})
    Page<DrinkReview> findByDrinkDrinkId(Long drinkId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "drink"})
    List<DrinkReview> findAllByUserAndDrink(User user, Drink drink);

    @Query("SELECT DISTINCT dr.drink FROM DrinkReview dr WHERE dr.user.userId = :userId")
    List<Drink> findDrinksByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "drink"})
    Page<DrinkReview> findAllByOrderByLikeCountDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "drink"})
    Page<DrinkReview> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<DrinkReview> findByDrinkReviewId(Long drinkReviewId);
}

