package com.ssafy.backend.domain.drink.service.subreview.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.SubReviewResponseDto;
import com.ssafy.backend.domain.drink.repository.SubReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubReviewQueryManager {
    private final SubReviewRepository subReviewRepository;

    public List<SubReviewResponseDto> findByDrinkReviewId(Long drinkReviewId) {
        return subReviewRepository.findByDrinkReviewDrinkReviewIdOrderByCreatedAtDesc(drinkReviewId)
                                  .stream()
                                  .map(SubReviewResponseDto::fromEntity)
                                  .collect(Collectors.toList());
    }
}