package com.ssafy.backend.domain.region.dto;

import com.ssafy.backend.domain.region.entity.Sido;

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
public class SidoResponseDto {
    public static SidoResponseDto fromEntity(Sido sido) {
        return SidoResponseDto.builder().sidoCode(sido.getSidoCode()).sidoName(sido.getSidoName()).build();
    }

    private Integer sidoCode;
    private String sidoName;
}
