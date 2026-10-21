package com.ssafy.backend.domain.drink.service.subreview;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.drink.dto.SubReviewRequestDto;
import com.ssafy.backend.domain.drink.dto.SubReviewResponseDto;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.entity.SubReview;
import com.ssafy.backend.domain.drink.service.subreview.manager.SubReviewManager;
import com.ssafy.backend.domain.drink.service.subreview.manager.SubReviewPushManager;
import com.ssafy.backend.domain.drink.service.subreview.manager.SubReviewQueryManager;
import com.ssafy.backend.domain.drink.service.subreview.validator.SubReviewValidator;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubReviewService {
    private final SubReviewValidator validator;
    private final SubReviewQueryManager queryManager;
    private final SubReviewManager subReviewManager;
    private final SubReviewPushManager pushManager;

    public List<SubReviewResponseDto> findByDrinkReviewId(Long drinkReviewId) {
        return queryManager.findByDrinkReviewId(drinkReviewId);
    }

    public SubReviewResponseDto writeSubReview(SubReviewRequestDto subReviewRequestDto, Long userId) {
        validator.validateContent(subReviewRequestDto.getContent());

        DrinkReview drinkReview = validator.validateAndGetDrinkReview(subReviewRequestDto.getDrinkReviewId());
        User user = validator.validateAndGetUser(userId);

        SubReview savedSubReview = subReviewManager.createSubReview(subReviewRequestDto, drinkReview, user);
        pushManager.sendSubReviewNotification(user, drinkReview);

        return SubReviewResponseDto.fromEntity(savedSubReview);
    }

    @Transactional
    public void deleteSubReview(Long subReviewId, Long userId) {
        SubReview subReview = validator.validateAndGetSubReview(subReviewId);
        User user = validator.validateAndGetUser(userId);
        validator.validateOwnership(subReview, user);

        subReviewManager.deleteSubReview(subReview);
    }

    @Transactional
    public SubReview updateSubReview(SubReviewRequestDto subReviewRequestDto, Long userId) {
        validator.validateContent(subReviewRequestDto.getContent());

        SubReview subReview = validator.validateAndGetSubReview(subReviewRequestDto.getSubReviewId());
        User user = validator.validateAndGetUser(userId);
        validator.validateOwnership(subReview, user);

        return subReviewManager.updateSubReview(subReview, subReviewRequestDto.getContent());
    }
}