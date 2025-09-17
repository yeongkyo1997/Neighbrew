package com.ssafy.backend.domain.drink.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DrinkUpdateRequestDto {
    private String name;
    private String image;
    private Float degree;
    private String description;
    private Long tagId;
}
