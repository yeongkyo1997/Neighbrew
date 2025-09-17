package com.ssafy.backend.domain.drink.service.drink;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.domain.drink.dto.DrinkRequestDto;
import com.ssafy.backend.domain.drink.dto.DrinkResponseDto;
import com.ssafy.backend.domain.drink.dto.DrinkUpdateRequestDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.service.drink.manager.DrinkImageManager;
import com.ssafy.backend.domain.drink.service.drink.manager.DrinkManager;
import com.ssafy.backend.domain.drink.service.drink.manager.DrinkQueryManager;
import com.ssafy.backend.domain.drink.service.drink.validator.DrinkValidator;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DrinkService {
    private final DrinkValidator validator;
    private final DrinkImageManager imageManager;
    private final DrinkManager drinkManager;
    private final DrinkQueryManager queryManager;

    public Page<DrinkResponseDto> findAll(Pageable pageable) {
        return queryManager.findAllDrinks(pageable);
    }

    public DrinkResponseDto save(DrinkRequestDto drinkRequestDto, MultipartFile multipartFile)
            throws IOException {
        validator.validateDrinkName(drinkRequestDto.getName());
        String imagePath = imageManager.uploadImageIfExists(multipartFile);
        Drink savedDrink = drinkManager.createDrink(drinkRequestDto, imagePath);
        return DrinkResponseDto.fromEntity(savedDrink);
    }

    public void deleteDrinkById(Long drinkId) {
        validator.validateDrinkExists(drinkId);
        drinkManager.deleteDrink(drinkId);
    }

    public DrinkResponseDto updateDrinkById(Long drinkId, DrinkUpdateRequestDto drinkUpdateRequestDto) {
        Drink existingDrink = validator.validateAndGetDrink(drinkId);
        Drink updatedDrink = drinkManager.updateDrink(existingDrink, drinkUpdateRequestDto);
        return DrinkResponseDto.fromEntity(updatedDrink);
    }

    public DrinkResponseDto getDrinkDetailsById(Long drinkId) {
        return queryManager.findDrinkById(drinkId);
    }

    public Page<DrinkResponseDto> searchDrinksByCriteria(String name, Long tagId, Pageable pageable) {
        return queryManager.searchDrinksByCriteria(name, tagId, pageable);
    }

    public List<DrinkResponseDto> findReviewedDrinksByUserId(Long userId) {
        return queryManager.findReviewedDrinksByUserId(userId);
    }
}