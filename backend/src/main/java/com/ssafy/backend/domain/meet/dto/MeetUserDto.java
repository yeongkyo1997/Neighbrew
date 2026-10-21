package com.ssafy.backend.domain.meet.dto;

import java.util.List;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetUserDto {
    private MeetRequestDto meetRequestDto;

    private List<PublicUserResponseDto> users;

    // 유저가 생성, 참여, 신처
    private List<Status> statuses;
}
