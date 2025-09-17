package com.ssafy.backend.domain.meet.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.domain.drink.dto.DrinkResponseDto;
import com.ssafy.backend.domain.region.dto.GugunResponseDto;
import com.ssafy.backend.domain.region.dto.SidoResponseDto;
import com.ssafy.backend.domain.user.dto.UserResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class MeetResponseDto {
    private Long meetId;
    private String meetName;
    private String description;
    private UserResponseDto host;
    private Integer nowParticipants;
    private Integer maxParticipants;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime meetDate;
    private Long tagId;
    private SidoResponseDto sido;
    private GugunResponseDto gugun;
    private Integer minAge;
    private Integer maxAge;
    private Float minLiverPoint;
    private DrinkResponseDto drink;
    private String imgSrc;
    private Long chatRoomId;
    private MeetStatus meetStatus;
}
