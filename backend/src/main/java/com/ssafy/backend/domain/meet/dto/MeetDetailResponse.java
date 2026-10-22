package com.ssafy.backend.domain.meet.dto;

import java.util.List;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;

// 모임 상세 응답. 참여자와 상태를 한 객체로 묶어 인덱스로 짝을 맞추지 않게 한다.
public record MeetDetailResponse(MeetResponseDto meet, List<Participant> participants) {
    public record Participant(PublicUserResponseDto user, Status status) {
    }
}
