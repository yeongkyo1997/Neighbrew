package com.ssafy.backend.domain.drink.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.domain.drink.dto.SubReviewRequestDto;
import com.ssafy.backend.domain.drink.dto.SubReviewResponseDto;
import com.ssafy.backend.domain.drink.service.subreview.SubReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/subreview")
@RequiredArgsConstructor
@Slf4j
public class SubReviewController {
    private final SubReviewService subReviewService;

    // 리뷰의 댓글을 조회하는 API
    @GetMapping("/list/{reviewId}")
    public ResponseEntity<List<SubReviewResponseDto>> getSubReviewsWithUserByDrinkReviewId(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok().body(subReviewService.findByDrinkReviewId(reviewId));
    }

    // 리뷰의 댓글을 작성하는 API
    @PostMapping("/write")
    public ResponseEntity<SubReviewResponseDto> writeSubReview(
            @RequestBody SubReviewRequestDto subReviewRequestDto,
            @LoginUser Long userId) {
        return ResponseEntity.ok().body(subReviewService.writeSubReview(subReviewRequestDto, userId));
    }

    // 댓글 삭제 API
    @DeleteMapping("/delete/{subReviewId}")
    public ResponseEntity<String> deleteSubReview(@PathVariable Long subReviewId,
                                                  @LoginUser Long userId) {
        // 삭제 성공
        subReviewService.deleteSubReview(subReviewId, userId);
        return ResponseEntity.ok().body("댓글 삭제 성공");
    }

    // 댓글을 수정하는 API
    @PutMapping("/update")
    public ResponseEntity<SubReviewResponseDto> updateSubReview(
            @RequestBody SubReviewRequestDto subReviewRequestDto,
            @LoginUser Long userId) {
        return ResponseEntity.ok().body(SubReviewResponseDto.fromEntity(
                subReviewService.updateSubReview(subReviewRequestDto, userId)));
    }
}
