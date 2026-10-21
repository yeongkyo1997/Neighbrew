package com.ssafy.backend.domain.drink.service.subreview.manager;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.SubReviewRequestDto;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.entity.SubReview;
import com.ssafy.backend.domain.drink.repository.SubReviewRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubReviewManager {
    private final SubReviewRepository subReviewRepository;

    public SubReview createSubReview(SubReviewRequestDto requestDto, DrinkReview drinkReview, User user) {
        SubReview subReview = SubReview.builder()
                                       .content(requestDto.getContent())
                                       .drinkReview(drinkReview)
                                       .user(user)
                                       .build();

        return subReviewRepository.save(subReview);
    }

    @Transactional
    public SubReview updateSubReview(SubReview subReview, String newContent) {
        subReview.update(newContent);
        return subReviewRepository.save(subReview);
    }

    @Transactional
    public void deleteSubReview(SubReview subReview) {
        subReviewRepository.delete(subReview);
    }
}