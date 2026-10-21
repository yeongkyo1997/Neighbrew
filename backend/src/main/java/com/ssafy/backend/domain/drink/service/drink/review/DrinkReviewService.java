package com.ssafy.backend.domain.drink.service.drink.review;

import java.io.IOException;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.domain.drink.dto.DrinkReviewRequestDto;
import com.ssafy.backend.domain.drink.dto.DrinkReviewResponseDto;
import com.ssafy.backend.domain.drink.dto.DrinkReviewUpdateDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.drink.service.drink.review.manager.DrinkReviewImageManager;
import com.ssafy.backend.domain.drink.service.drink.review.manager.DrinkReviewManager;
import com.ssafy.backend.domain.drink.service.drink.review.manager.DrinkReviewQueryManager;
import com.ssafy.backend.domain.drink.service.drink.review.validator.DrinkReviewValidator;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DrinkReviewService {
    private final DrinkReviewValidator validator;
    private final DrinkReviewImageManager imageManager;
    private final DrinkReviewManager reviewManager;
    private final DrinkReviewQueryManager queryManager;

    public DrinkReviewResponseDto createDrinkReview(DrinkReviewRequestDto drinkReviewRequestDto,
                                                    MultipartFile multipartFile) throws IOException {
        String imagePath = imageManager.uploadImageIfExists(multipartFile);

        User user = validator.validateAndGetUser(drinkReviewRequestDto.getUserId());
        Drink drink = validator.validateAndGetDrink(drinkReviewRequestDto.getDrinkId());

        DrinkReview drinkReview = reviewManager.createDrinkReview(drinkReviewRequestDto, user, drink,
                                                                  imagePath);

        return DrinkReviewResponseDto.fromEntity(drinkReview);
    }

    public Page<DrinkReviewResponseDto> findDrinkReviewByDrinkDrinkId(Long drinkId, Pageable pageable) {
        return queryManager.findReviewsByDrinkId(drinkId, pageable);
    }

    public List<DrinkReviewResponseDto> findReviewsByUserIdAndDrinkId(Long userId, Long drinkId) {
        User user = validator.validateAndGetUser(userId);
        Drink drink = validator.validateAndGetDrink(drinkId);

        return queryManager.findReviewsByUserAndDrink(user, drink);
    }

    @Transactional
    public void deleteDrinkReview(Long drinkReviewId, Long userId) {
        DrinkReview drinkReview = validator.validateAndGetDrinkReview(drinkReviewId);
        validator.validateReviewOwnership(drinkReview, userId);

        imageManager.deleteImageIfNotDefault(drinkReview.getImg());
        reviewManager.deleteDrinkReview(drinkReviewId);
    }

    public DrinkReviewResponseDto updateDrinkReview(Long drinkReviewId, DrinkReviewUpdateDto request,
                                                    MultipartFile multipartFile, Long userId)
            throws IOException {
        validator.validateUpdateRequest(drinkReviewId, request);

        DrinkReview drinkReview = validator.validateAndGetDrinkReview(request.getDrinkReviewId());
        validator.validateReviewOwnership(drinkReview, userId);

        String imagePath = imageManager.handleImageUpdate(drinkReview, request, multipartFile);
        DrinkReview updatedReview = reviewManager.updateDrinkReviewContent(drinkReview, request.getContent(),
                                                                           imagePath);

        return DrinkReviewResponseDto.fromEntity(updatedReview);
    }

    public Page<DrinkReviewResponseDto> findDrinkReviewByOrderByLikeCountDesc(Pageable pageable) {
        return queryManager.findReviewsOrderByLikeCount(pageable);
    }

    public Page<DrinkReviewResponseDto> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return queryManager.findReviewsOrderByCreatedAt(pageable);
    }

    public DrinkReviewResponseDto findReviewByDrinkReviewId(Long drinkReviewId) {
        return queryManager.findReviewById(drinkReviewId);
    }
}
