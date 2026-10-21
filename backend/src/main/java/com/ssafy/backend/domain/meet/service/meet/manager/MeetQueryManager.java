package com.ssafy.backend.domain.meet.service.meet.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.ssafy.backend.domain.meet.dto.MeetUserDto;
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

    public Map<String, Object> findMeetdetailByMeetId(Long meetId) {
        List<MeetUser> meetUsers = findMeetUsersByMeetId(meetId);
        List<PublicUserResponseDto> users = toPublicUsers(meetUsers);
        List<Status> statuses = meetUsers.stream().map(MeetUser::getStatus).collect(Collectors.toList());
        return createResultMap(findByMeetId(meetId), users, statuses);
    }

    private Map<String, Object> createResultMap(MeetResponseDto meet, List<PublicUserResponseDto> users,
                                                List<Status> statuses) {
        Map<String, Object> result = new HashMap<>();
        result.put("meet", meet);
        result.put("users", users);
        result.put("statuses", statuses);
        return result;
    }

    public MeetResponseDto findByMeetId(Long meetId) {
        Meet findMeet = meetRepository.findById(meetId).orElseThrow(
                () -> new IllegalArgumentException("미팅 정보가 올바르지 않습니다."));
        return findMeet.toMeetResponseDto(findSidoById(findMeet.getSidoCode()),
                                          findGugunByCodes(findMeet.getSidoCode(), findMeet.getGugunCode()));
    }

    public MeetUserDto findMeetUserByMeetId(Long meetId) {
        List<MeetUser> meetUsers = findMeetUsersByMeetId(meetId);
        MeetUserDto meetUserDto = MeetUserDto.builder().build();

        if (!meetUsers.isEmpty()) {
            setMeetRequestDtoFromFirstMeetUser(meetUserDto, meetUsers.get(0));
            addUsersAndStatusesToDto(meetUserDto, meetUsers);
        }

        return meetUserDto;
    }

    private void setMeetRequestDtoFromFirstMeetUser(MeetUserDto meetUserDto, MeetUser firstMeetUser) {
        meetUserDto.setMeetRequestDto(firstMeetUser.getMeet().toDto());
    }

    private void addUsersAndStatusesToDto(MeetUserDto meetUserDto, List<MeetUser> meetUsers) {
        List<Status> statuses = meetUsers.stream().map(MeetUser::getStatus).collect(Collectors.toList());
        meetUserDto.setUsers(toPublicUsers(meetUsers));
        meetUserDto.setStatuses(statuses);
    }

    private List<PublicUserResponseDto> toPublicUsers(List<MeetUser> meetUsers) {
        return meetUsers.stream().map(MeetUser::getUser).map(PublicUserResponseDto::fromEntity)
                        .collect(Collectors.toList());
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