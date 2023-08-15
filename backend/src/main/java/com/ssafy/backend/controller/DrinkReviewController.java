package com.ssafy.backend.controller;

import com.ssafy.backend.dto.drinkReview.DrinkReviewRequestDto;
import com.ssafy.backend.dto.drinkReview.DrinkReviewResponseDto;
import com.ssafy.backend.dto.drinkReview.DrinkReviewUpdateDto;
import com.ssafy.backend.service.DrinkReviewService;
import com.ssafy.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drinkreview")
public class DrinkReviewController {
    private final DrinkReviewService drinkReviewService;

    @GetMapping("/{drinkId}")
    public ResponseEntity<Page<DrinkReviewResponseDto>> getReviewsByDrinkId(@PathVariable Long drinkId, Pageable pageable) {
        return ResponseEntity.ok(drinkReviewService.getReviewsByDrinkId(drinkId, pageable));
    }

    // 리뷰 아이디로 리뷰 가져오기
    @GetMapping("/review/{drinkReviewId}")
    public ResponseEntity<DrinkReviewResponseDto> getReviewByDrinkReviewId(@PathVariable Long drinkReviewId) {
        return ResponseEntity.ok(drinkReviewService.getReviewByDrinkReviewId(drinkReviewId));
    }

    @GetMapping("/{drinkId}/{userId}")
    public ResponseEntity<List<DrinkReviewResponseDto>> getReviewByUserIdAndDrinkId(@PathVariable Long drinkId, @PathVariable Long userId) {
        return ResponseEntity.ok(drinkReviewService.getReviewsByUserIdAndDrinkId(userId, drinkId));
    }

    // 좋아요 많은 순으로 리뷰 가져오기
    @GetMapping("/likes")
    public ResponseEntity<Page<DrinkReviewResponseDto>> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return ResponseEntity.ok(drinkReviewService.findAllByOrderByCreatedAtDesc(pageable));
    }

    @PostMapping("")
    public ResponseEntity<DrinkReviewResponseDto> createDrinkReview(@RequestHeader("Authorization") String token,
                                                                    @ModelAttribute DrinkReviewRequestDto drinkReviewRequestDto,
                                                                    @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {

        Long userId = JwtUtil.parseUserIdFromToken(token);
        drinkReviewRequestDto.setUserId(userId);

        return ResponseEntity.ok().body(drinkReviewService.createDrinkReview(drinkReviewRequestDto, multipartFile));
    }

    @PutMapping("/{drinkReviewId}/{userId}")
    public ResponseEntity<DrinkReviewResponseDto> updateDrinkReview(@PathVariable Long drinkReviewId,
                                                                    @ModelAttribute DrinkReviewUpdateDto drinkReviewUpdateDto,
                                                                    @RequestPart(value = "image", required = false) Optional<MultipartFile> multipartFile,
                                                                    @PathVariable Long userId,
                                                                    @RequestHeader("Authorization") String token) throws IOException {
        JwtUtil.validateToken(token, userId);
        checkCapacityFile(multipartFile);
        return ResponseEntity.ok().body(drinkReviewService.updateDrinkReview(drinkReviewId, drinkReviewUpdateDto, multipartFile.orElse(null), userId));
    }

    private void checkCapacityFile(Optional<MultipartFile> multipartFile) {
        if (multipartFile.isPresent()) {
            if (multipartFile.get().getSize() > 1024 * 1024 * 20)
                throw new IllegalArgumentException("파일 업로드 크기는 20MB로 제한되어 있습니다.");
        }
    }

    @DeleteMapping("/{drinkReviewId}")
    public ResponseEntity<String> deleteDrinkReview(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long drinkReviewId) throws IllegalArgumentException {
        Long userId = JwtUtil.parseUserIdFromToken(token);
        drinkReviewService.deleteDrinkReview(drinkReviewId, userId);
        return ResponseEntity.ok("삭제 완료");
    }
}
