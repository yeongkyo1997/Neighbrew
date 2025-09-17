package com.ssafy.backend.domain.meet.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.entity.Meet;

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
public class MeetRequestDto {
    private Long meetId;
    private String meetName;
    private String description;
    private Long hostId;
    private Integer nowParticipants;
    private Integer maxParticipants;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime meetDate;
    private Long tagId;
    private Integer sidoCode;
    private Integer gugunCode;
    private Integer minAge;
    private Integer maxAge;
    private Float minLiverPoint;
    private Drink drink;
    private String imgSrc;
    private Long chatRoomId;

    public Meet toEntity() {
        return Meet.builder().meetName(meetName).description(description).nowParticipants(nowParticipants)
                   .maxParticipants(maxParticipants).meetDate(meetDate).sidoCode(sidoCode).gugunCode(gugunCode)
                   .minAge(minAge).maxAge(maxAge).minLiverPoint(minLiverPoint).drink(drink).imgSrc(imgSrc)
                   .build();
    }
}
