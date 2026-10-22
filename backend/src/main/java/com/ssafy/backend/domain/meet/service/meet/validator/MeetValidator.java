package com.ssafy.backend.domain.meet.service.meet.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.repository.TagRepository;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.service.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetValidator {
    private final UserReader userReader;
    private final TagRepository tagRepository;
    private final DrinkRepository drinkRepository;
    private final MeetRepository meetRepository;

    public void validateTagId(Long tagId) {
        if (tagId > 7L || tagId < 0L) {
            throw new IllegalArgumentException("태그ID가 존재하지 않습니다.");
        }
    }

    public void validateMeet(MeetRequestDto meetRequestDto, Long drinkId) {
        if (meetRequestDto.getMeetName() == null) {
            throw new IllegalArgumentException("모임 이름이 등록되지 않았습니다.");
        }
        if (meetRequestDto.getMeetDate() == null) {
            throw new IllegalArgumentException("모임 날짜 정보가 누락되었습니다.");
        }
        if (meetRequestDto.getMaxParticipants() == null) {
            throw new IllegalArgumentException("모임 최대 인원 수용 정보가 입력되지 않았습니다.");
        }
        if (meetRequestDto.getMaxParticipants() > 8) {
            throw new IllegalArgumentException("모임 최대 인원 수용치를 초과했습니다.");
        }
        if (drinkId == null) {
            throw new IllegalArgumentException("모임에 등록할 술 정보가 포함되지 않았습니다.");
        }
        if (meetRequestDto.getTagId() == null) {
            throw new IllegalArgumentException("모임에 등록할 태그 정보가 포함되지 않았습니다.");
        }
        if (meetRequestDto.getMinAge() < 20) {
            throw new IllegalArgumentException("모임 최소나이를 다시 입력해 주세요.");
        }
        if (meetRequestDto.getMinAge() >= 200) {
            throw new IllegalArgumentException("모임 최대 나이를 다시 입력해 주세요.");
        }
        if (meetRequestDto.getMeetDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("모임 날짜 및 시간을 확인해 주세요.");
        }
    }

    public User validateAndGetUser(Long userId) {
        return userReader.getById(userId);
    }

    public Tag validateAndGetTag(Long tagId) {
        return tagRepository.findById(tagId).orElseThrow(
                () -> new IllegalArgumentException("올바른 태그 정보가 입력되지 않았습니다."));
    }

    public Drink validateAndGetDrink(Long drinkId) {
        return drinkRepository.findById(drinkId).orElseThrow(
                () -> new IllegalArgumentException("올바른 술 정보가 입력되지 않았습니다."));
    }

    public Meet validateAndGetMeet(Long meetId) {
        return meetRepository.findById(meetId).orElseThrow(
                () -> new IllegalArgumentException("해당 미팅 정보를 찾을 수 없습니다."));
    }

    // 참여 인원을 바꿀 때 쓴다. 트랜잭션이 끝날 때까지 모임 행을 잠근다.
    public Meet validateAndGetMeetForUpdate(Long meetId) {
        return meetRepository.findByIdForUpdate(meetId).orElseThrow(
                () -> new IllegalArgumentException("해당 미팅 정보를 찾을 수 없습니다."));
    }

    public void validateHostPermission(Meet meet, Long hostId) {
        if (!meet.getHost().getUserId().equals(hostId)) {
            throw new IllegalArgumentException("방장만 요청할 수 있습니다.");
        }
    }
}