package com.ssafy.backend.domain.drink.service.drink.review.manager;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.domain.drink.dto.DrinkReviewUpdateDto;
import com.ssafy.backend.domain.drink.entity.DrinkReview;
import com.ssafy.backend.domain.storage.service.s3.S3Service;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkReviewImageManager {
    private static final String DEFAULT_IMAGE = "no image";

    private final S3Service s3Service;

    public String uploadImageIfExists(MultipartFile multipartFile) throws IOException {
        return (multipartFile != null && !multipartFile.isEmpty()) ? s3Service.upload(UploadType.DRINKREVIEW,
                                                                                      multipartFile) :
               DEFAULT_IMAGE;
    }

    public void deleteImageIfNotDefault(String imagePath) {
        if (isNotDefaultImage(imagePath)) {
            s3Service.deleteImg(imagePath);
        }
    }

    public String handleImageUpdate(DrinkReview drinkReview, DrinkReviewUpdateDto request,
                                    MultipartFile multipartFile) throws IOException {
        if (multipartFile != null) {
            deleteImageIfNotDefault(drinkReview.getImg());
            return s3Service.upload(UploadType.DRINKREVIEW, multipartFile);
        } else {
            return request.getImgSrc() != null ? request.getImgSrc() : drinkReview.getImg();
        }
    }

    private boolean isNotDefaultImage(String imagePath) {
        return !DEFAULT_IMAGE.equals(imagePath);
    }
}