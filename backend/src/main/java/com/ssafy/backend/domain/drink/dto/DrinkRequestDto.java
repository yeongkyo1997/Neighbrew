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
public class DrinkRequestDto {
    private String name;
    private String imgSrc;
    private Float degree;
    private String description;
    private Long tagId;

    public Drink toEntity() {
        return Drink.builder().name(name).image(imgSrc).degree(degree).description(description).tagId(tagId)
                    .build();
    }
}
