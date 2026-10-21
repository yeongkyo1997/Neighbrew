package com.ssafy.backend.domain.drink.dto;

import com.ssafy.backend.domain.drink.entity.SubReview;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SubReviewResponseDto {
    public static SubReviewResponseDto fromEntity(SubReview subReview) {
        return SubReviewResponseDto.builder().subReviewId(subReview.getSubReviewId()).content(
                subReview.getContent()).user(PublicUserResponseDto.fromEntity(subReview.getUser())).build();
    }

    private Long subReviewId;
    private String content;
    private PublicUserResponseDto user; // User 정보를 포함하기 위해
}
