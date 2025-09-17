package com.ssafy.backend.domain.drink.dto;

import com.ssafy.backend.domain.drink.entity.Drink;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DrinkResponseDto {
    public static DrinkResponseDto fromEntity(Drink drink) {
        return DrinkResponseDto.builder().drinkId(drink.getDrinkId()).name(drink.getName()).image(
                drink.getImage()).degree(drink.getDegree()).description(drink.getDescription()).tagId(
                drink.getTagId()).build();
    }

    private Long drinkId;
    private String name;
    private String image;
    private Float degree;
    private String description;
    private Long tagId;

}