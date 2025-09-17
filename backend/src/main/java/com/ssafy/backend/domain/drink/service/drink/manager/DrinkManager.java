package com.ssafy.backend.domain.drink.service.drink.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.drink.dto.DrinkRequestDto;
import com.ssafy.backend.domain.drink.dto.DrinkUpdateRequestDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DrinkManager {
    private final DrinkRepository drinkRepository;

    public Drink createDrink(DrinkRequestDto requestDto, String imagePath) {
        DrinkRequestDto updatedRequestDto = DrinkRequestDto.builder()
                                                           .name(requestDto.getName())
                                                           .imgSrc(imagePath)
                                                           .degree(requestDto.getDegree())
                                                           .description(requestDto.getDescription())
                                                           .tagId(requestDto.getTagId())
                                                           .build();

        return drinkRepository.save(updatedRequestDto.toEntity());
    }

    public Drink updateDrink(Drink existingDrink, DrinkUpdateRequestDto updateRequestDto) {
        Drink updatedDrink = Drink.builder()
                                  .drinkId(existingDrink.getDrinkId())
                                  .name(updateRequestDto.getName() != null ? updateRequestDto.getName() :
                                        existingDrink.getName())
                                  .image(updateRequestDto.getImage() != null ? updateRequestDto.getImage() :
                                         existingDrink.getImage())
                                  .degree(updateRequestDto.getDegree() != null ? updateRequestDto.getDegree() :
                                          existingDrink.getDegree())
                                  .description(updateRequestDto.getDescription() != null ?
                                               updateRequestDto.getDescription() :
                                               existingDrink.getDescription())
                                  .tagId(updateRequestDto.getTagId() != null ? updateRequestDto.getTagId() :
                                         existingDrink.getTagId())
                                  .build();

        return drinkRepository.save(updatedDrink);
    }

    public void deleteDrink(Long drinkId) {
        drinkRepository.deleteById(drinkId);
    }
}