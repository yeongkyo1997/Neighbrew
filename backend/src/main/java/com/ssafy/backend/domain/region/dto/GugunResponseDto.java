package com.ssafy.backend.domain.region.dto;

import com.ssafy.backend.domain.region.entity.Gugun;

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
public class GugunResponseDto {
    public static GugunResponseDto fromEntity(Gugun gugun) {
        return GugunResponseDto.builder()
                               .gugunCode(gugun.getGugunCode())
                               .gugunName(gugun.getGugunName())
                               .sidoCode(gugun.getSidoCode())
                               .build();
    }

    private Integer gugunCode;
    private String gugunName;
    private Integer sidoCode;
}
