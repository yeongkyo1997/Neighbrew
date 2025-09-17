package com.ssafy.backend.domain.drink.service.drink.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.DrinkResponseDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkQueryManager {
    private final DrinkRepository drinkRepository;
    private final DrinkReviewRepository drinkReviewRepository;

    public Page<DrinkResponseDto> findAllDrinks(Pageable pageable) {
        Page<Drink> drinks = drinkRepository.findAll(pageable);
        return drinks.map(DrinkResponseDto::fromEntity);
    }

    public DrinkResponseDto findDrinkById(Long drinkId) {
        Drink drink = drinkRepository.findById(drinkId)
                                     .orElseThrow(() -> new IllegalArgumentException("해당 술이 없습니다."));
        return DrinkResponseDto.fromEntity(drink);
    }

    public Page<DrinkResponseDto> searchDrinksByCriteria(String name, Long tagId, Pageable pageable) {
        Page<Drink> drinks;

        if (name == null && tagId == 0) {
            drinks = drinkRepository.findAll(pageable);
        } else if (name == null) {
            drinks = drinkRepository.findByTagId(tagId, pageable).orElse(Page.empty());
        } else if (tagId == 0) {
            drinks = drinkRepository.findByNameContains(name, pageable).orElse(Page.empty());
        } else {
            drinks = drinkRepository.findByNameContainsAndTagId(name, tagId, pageable).orElse(Page.empty());
        }

        return drinks.map(DrinkResponseDto::fromEntity);
    }

    public List<DrinkResponseDto> findReviewedDrinksByUserId(Long userId) {
        return drinkReviewRepository.findDrinksByUserId(userId)
                                    .stream()
                                    .map(DrinkResponseDto::fromEntity)
                                    .collect(Collectors.toList());
    }
}