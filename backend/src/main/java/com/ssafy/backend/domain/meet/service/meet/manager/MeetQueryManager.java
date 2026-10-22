package com.ssafy.backend.domain.meet.service.meet.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.meet.dto.MeetResponseDto;
import com.ssafy.backend.domain.meet.dto.MeetDetailResponse;
import com.ssafy.backend.domain.meet.dto.MeetDetailResponse.Participant;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.region.dto.GugunResponseDto;
import com.ssafy.backend.domain.region.dto.SidoResponseDto;
import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.repository.GugunRepository;
import com.ssafy.backend.domain.region.repository.SidoRepository;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;
import com.ssafy.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetQueryManager {
    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;
    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;
    private final ModelMapper modelMapper;

    public Page<MeetResponseDto> findMeetsByTagId(Long tagId, Pageable pageable) {
        return tagId == 0L ? findAll(pageable) : findByTagId(tagId, pageable);
    }

    private Page<MeetResponseDto> findAll(Pageable pageable) {
        return getMeetSearchDtos(
                meetRepository.findByMeetStatusNotOrderByCreatedAtDesc(MeetStatus.END, pageable));
    }

    private Page<MeetResponseDto> findByTagId(Long tagId, Pageable pageable) {
        return getMeetSearchDtos(
                meetRepository.findByTagTagIdAndMeetStatusNotOrderByCreatedAtDesc(tagId, MeetStatus.END,
                                                                                  pageable));
    }

    private Page<MeetResponseDto> getMeetSearchDtos(Page<Meet> data) {
        return data.map(meet -> {
            MeetResponseDto meetResponseDto = modelMapper.map(meet, MeetResponseDto.class);
            meetResponseDto.setHost(PublicUserResponseDto.fromEntity(meet.getHost()));
            meetResponseDto.setSido(SidoResponseDto.fromEntity(findSidoById(meet.getSidoCode())));
            meetResponseDto.setGugun(
                    GugunResponseDto.fromEntity(findGugunByCodes(meet.getSidoCode(), meet.getGugunCode())));
            return meetResponseDto;
        });
    }

    public MeetDetailResponse findMeetdetailByMeetId(Long meetId) {
        MeetResponseDto meet = findByMeetId(meetId);
        List<Participant> participants = findMeetUsersByMeetId(meetId).stream().map(
                meetUser -> new Participant(PublicUserResponseDto.fromEntity(meetUser.getUser()),
                                            meetUser.getStatus())).toList();
        return new MeetDetailResponse(meet, participants);
    }

    public MeetResponseDto findByMeetId(Long meetId) {
        Meet findMeet = meetRepository.findById(meetId).orElseThrow(
                () -> new NotFoundException("모임을 찾을 수 없습니다. id=" + meetId));
        return findMeet.toMeetResponseDto(findSidoById(findMeet.getSidoCode()),
                                          findGugunByCodes(findMeet.getSidoCode(), findMeet.getGugunCode()));
    }

    public Map<String, List<MeetResponseDto>> findUserMeetByUserId(Long userId) {
        Map<String, List<MeetResponseDto>> userMeets = Arrays.stream(Status.values()).collect(
                Collectors.toMap(Enum::name, status -> new ArrayList<>(), (a, b) -> b));

        List<MeetUser> meetUsers = meetUserRepository.findByUserUserIdOrderByMeetCreatedAtDesc(userId);

        for (MeetUser meetUser : meetUsers) {
            MeetResponseDto responseDto = toMeetResponseDto(meetUser);
            userMeets.get(meetUser.getStatus().name()).add(responseDto);
        }

        return userMeets;
    }

    private MeetResponseDto toMeetResponseDto(MeetUser meetUser) {
        Sido findSido = findSidoById(meetUser.getMeet().getSidoCode());
        Gugun findGugun = findGugunByCodes(meetUser.getMeet().getSidoCode(), meetUser.getMeet().getGugunCode());
        return meetUser.getMeet().toMeetResponseDto(findSido, findGugun);
    }

    private List<MeetUser> findMeetUsersByMeetId(Long meetId) {
        return meetUserRepository.findByMeetMeetIdOrderByStatusDesc(meetId);
    }

    private Sido findSidoById(Integer sidoCode) {
        return sidoRepository.findById(sidoCode).orElseThrow(
                () -> new IllegalArgumentException("시도 정보를 찾을 수 없습니다."));
    }

    private Gugun findGugunByCodes(Integer sidoCode, Integer gugunCode) {
        return gugunRepository.findBySidoCodeAndGugunCode(sidoCode, gugunCode).orElseThrow(
                () -> new IllegalArgumentException("구군 정보를 찾을 수 없습니다."));
    }
}