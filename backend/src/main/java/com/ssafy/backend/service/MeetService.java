package com.ssafy.backend.service;


import com.ssafy.backend.Enum.PushType;
import com.ssafy.backend.Enum.Status;
import com.ssafy.backend.Enum.UploadType;
import com.ssafy.backend.dto.MeetDto;
import com.ssafy.backend.dto.MeetUserDto;
import com.ssafy.backend.entity.Follow;
import com.ssafy.backend.entity.Meet;
import com.ssafy.backend.entity.MeetUser;
import com.ssafy.backend.entity.User;
import com.ssafy.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;
    private final TagRepository tagRepository;
    private final DrinkRepository drinkRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;
    private final MeetUserService meetUserService;
    private final PushService pushService;
    private final FollowService followService;


    public List<MeetDto> findAll() {
        List<Meet> list = meetRepository.findAll();

        List<MeetDto> dtos = new ArrayList<>();
        for (Meet meet : list) {
            dtos.add(MeetDto.builder()
                    .meetId(meet.getMeetId())
                    .meetName(meet.getMeetName())
                    .description(meet.getDescription())
                    .hostId(meet.getHostId())
                    .nowParticipants(meet.getNowParticipants())
                    .maxParticipants(meet.getMaxParticipants())
                    .meetDate(meet.getMeetDate())
                    .tagId(meet.getTag().getTagId())
                    .sido(meet.getSido())
                    .gugun(meet.getGugun())
                    .dong(meet.getDong())
                    .minAge(meet.getMinAge())
                    .maxAge(meet.getMaxAge())
                    .minLiverPoint(meet.getMinLiverPoint())
                    .drink(meet.getDrink())
                    .imgSrc(meet.getImgSrc())
                    .build());
        }
        return dtos;
    }

    public MeetUserDto findMeetUserByMeetId(Long meetId) {
        log.info("meetId : {}인 모임 정보 출력 ", meetId);

        List<MeetUser> meetUsers = meetUserRepository.findByMeet_MeetIdOrderByStatusDesc(meetId).orElseThrow(() -> new IllegalArgumentException("모임 ID 값이 올바르지 않습니다."));


        MeetUserDto meetUserDto = MeetUserDto.builder().build();

        if (meetUsers.size() != 0) {
            meetUserDto.setMeetDto(meetUsers.get(0).getMeet().toDto());
            for (MeetUser mu : meetUsers) {
                meetUserDto.getUsers().add(mu.getUser());
                meetUserDto.getStatuses().add(mu.getStatus());
            }
        }
        return meetUserDto;
    }

    public Meet findByMeetId(Long meetId) {
        return meetRepository.findById(meetId).orElseThrow(() -> new IllegalArgumentException("미팅 정보가 올바르지 않습니다."));
    }

    public Map<String, List<MeetDto>> findByUserId(Long userId) {
        Map<String, List<MeetDto>> userMeets = new HashMap<>();
        userMeets.put(Status.APPLY.name(), new ArrayList<>());
        userMeets.put(Status.GUEST.name(), new ArrayList<>());
        userMeets.put(Status.HOST.name(), new ArrayList<>());

        List<MeetUser> meetUsers = meetUserRepository.findByUser_UserIdOrderByStatus(userId).orElseThrow(() -> new IllegalArgumentException("유저ID 값이 올바르지 않습니다."));

        for (MeetUser mu : meetUsers) {
            Status status = mu.getStatus();

            if (status != Status.FINISH)
                userMeets.get(status.name()).add(mu.getMeet().toDto());
        }

        return userMeets;
    }

    public Meet saveMeet(MeetDto meetDto, Long userId, Long drinkId, MultipartFile multipartFile) {
        meetDto.setHostId(userId);
        meetDto.setNowParticipants(1);
        log.info("모임 생성 : {} ", meetDto);

        try {
            boolean imgExist = !multipartFile.getOriginalFilename().equals("");
            if (imgExist) meetDto.setImgSrc(s3Service.upload(UploadType.MEET, multipartFile));

            Meet meet = meetDto.toEntity();
            meet.setTag(tagRepository.findById(meetDto.getTagId()).orElseThrow(() -> new IllegalArgumentException("잘못된 태그 정보 입니다.")));
            meet.setDrink(drinkRepository.findById(drinkId).orElseThrow(() -> new IllegalArgumentException("잘못된 주종 정보 입니다.")));

            Meet createdMeet = meetRepository.save(meet);
            User hostUser = userRepository.findByUserId(meetDto.getHostId()).orElseThrow(() -> new IllegalArgumentException("유저 정보가 올바르지 않습니다."));

            //MeetUser 정보를 추가한다.
            meetUserService.saveMeetUser(createdMeet, hostUser, Status.HOST);

            //팔로워에게 메세지를 보낸다
            List<Follow> followers = followService.findByFollower(userId);
            log.info("방장");
            for (Follow fw : followers) {
                StringBuilder pushMessage = new StringBuilder();
                pushMessage.append(hostUser.getName()).append("님께서 회원님께서 모임(").append(createdMeet.getMeetName()).append(")을 생성했습니다.");
                pushService.send(hostUser, fw.getFollower(), PushType.CREATEMEET, pushMessage.toString(), "이동할 url");
            }

            return createdMeet;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMeet(Long meetId, MeetDto meetDto, Long drinkId) {
        log.info("meetId : {}인 모임 정보 업데이트 : {} ", meetId, meetDto);

        Meet findMeet = meetRepository.findById(meetId).orElseThrow(() -> new IllegalArgumentException("해당 미팅 정보를 찾을 수 없습니다."));
        findMeet.update(meetDto.toEntity());
        findMeet.setTag(tagRepository.findById(meetDto.getTagId()).orElseThrow(() -> new IllegalArgumentException("잘못된 태그 정보 입니다.")));
        findMeet.setDrink(drinkRepository.findById(drinkId).orElseThrow(() -> new IllegalArgumentException("잘못된 주종 정보 입니다.")));
        log.info("findMeet 업데이트 했당: {} ", findMeet);


        meetRepository.save(findMeet);
    }

    public void deleteMeet(Long meetId) {
        log.info("meetId : {}인 모임 삭제", meetId);

        meetRepository.findById(meetId).ifPresent(meet -> meetRepository.delete(meet));
    }

    public void updateParticipants(Long meetId) {
        Meet findMeet = meetRepository.findById(meetId).orElseThrow(() -> new IllegalArgumentException("해당 미팅 정보를 찾을 수 없습니다."));
        findMeet.setNowParticipants(findMeet.getNowParticipants() + 1);

        meetRepository.save(findMeet);
    }
}
