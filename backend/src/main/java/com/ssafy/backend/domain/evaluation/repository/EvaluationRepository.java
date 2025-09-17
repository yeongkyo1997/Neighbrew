package com.ssafy.backend.domain.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.evaluation.entity.Evaluation;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByRatedUserAndReviewerAndMeet(User ratedUser, User reviewer, Meet meet);
}