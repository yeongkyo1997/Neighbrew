package com.ssafy.backend.domain.meet.service.meet.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetManager {
    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;

    public Meet saveMeetEntity(MeetRequestDto meetRequestDto, Drink drink, Tag tag, User host,
                               ChatRoom chatRoom) {
        Meet meet = meetRequestDto.toEntity();
        meet.setHost(host);
        meet.setTag(tag);
        meet.setDrink(drink);
        meet.setChatRoom(chatRoom);
        return meetRepository.save(meet);
    }

    public Meet updateMeet(Long meetId, MeetRequestDto meetRequestDto, Drink drink, Tag tag) {
        Meet updateMeet = meetRepository.findById(meetId).orElseThrow(
                () -> new IllegalArgumentException("해당 미팅 정보를 찾을 수 없습니다."));

        updateMeet.update(meetRequestDto.toEntity());
        updateMeet.setMeetId(meetId);
        updateMeet.setTag(tag);
        updateMeet.setDrink(drink);

        return meetRepository.save(updateMeet);
    }

    public void deleteMeet(Long meetId) {
        meetRepository.findById(meetId).ifPresent(meetRepository::delete);
    }

    // meet은 validateAndGetMeetForUpdate로 잠근 엔티티여야 한다.
    public void increaseParticipants(Meet meet) {
        meet.addParticipant();
        meetRepository.save(meet);
    }

    public void decreaseParticipants(Meet meet) {
        meet.removeParticipant();
        meetRepository.save(meet);
    }

    public List<Meet> findExpiredMeets() {
        return meetRepository.findMeetByMeetDateBefore();
    }

    public void updateMeetsToEnded(List<Meet> meets) {
        List<Meet> updateMeetList = new ArrayList<>();
        for (Meet meet : meets) {
            if (meet.getMeetStatus() != MeetStatus.END) {
                meet.setMeetStatus(MeetStatus.END);
                updateMeetList.add(meet);
            }
        }
        if (!updateMeetList.isEmpty()) {
            meetRepository.saveAll(updateMeetList);
        }
    }

    public List<MeetUser> findMeetUsersByMeetId(Long meetId) {
        return meetUserRepository.findByMeetMeetIdOrderByStatusDesc(meetId);
    }
}