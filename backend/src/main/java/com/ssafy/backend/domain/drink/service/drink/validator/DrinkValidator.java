package com.ssafy.backend.domain.drink.service.drink.validator;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.exception.NotFoundException;

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
        return drinkRepository.findById(drinkId).orElseThrow(
                () -> new NotFoundException("술을 찾을 수 없습니다. id=" + drinkId));
    }

    public void validateDrinkExists(Long drinkId) {
        if (!drinkRepository.existsById(drinkId)) {
            throw new NotFoundException("술을 찾을 수 없습니다. id=" + drinkId);
        }
    }
}