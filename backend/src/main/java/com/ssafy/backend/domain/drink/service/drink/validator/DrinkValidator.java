package com.ssafy.backend.domain.drink.service.drink.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkValidator {
    private final DrinkRepository drinkRepository;

    public void validateDrinkName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("술 이름이 없습니다.");
        }
    }

    public Drink validateAndGetDrink(Long drinkId) {
        return drinkRepository.findById(drinkId)
                              .orElseThrow(() -> new IllegalArgumentException("해당 술이 없습니다."));
    }

    public void validateDrinkExists(Long drinkId) {
        if (!drinkRepository.existsById(drinkId)) {
            throw new IllegalArgumentException("해당 술이 없습니다.");
        }
    }
}