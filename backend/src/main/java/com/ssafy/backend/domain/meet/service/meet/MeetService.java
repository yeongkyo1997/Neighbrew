package com.ssafy.backend.domain.meet.service.meet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.dto.MeetResponseDto;
import com.ssafy.backend.domain.meet.dto.MeetUserDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetChatManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetImageManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetParticipantManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetPushManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetQueryManager;
import com.ssafy.backend.domain.meet.service.meet.validator.MeetValidator;
import com.ssafy.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetValidator validator;
    private final MeetImageManager imageManager;
    private final MeetQueryManager queryManager;
    private final MeetManager meetManager;
    private final MeetParticipantManager participantManager;
    private final MeetPushManager pushManager;
    private final MeetChatManager chatManager;
    private final MeetUserService meetUserService;

    public Page<MeetResponseDto> findMeetsByTagId(Long tagId, Pageable pageable) {
        validator.validateTagId(tagId);
        return queryManager.findMeetsByTagId(tagId, pageable);
    }

    public Map<String, Object> findMeetdetailByMeetId(Long meetId) {
        return queryManager.findMeetdetailByMeetId(meetId);
    }

    public Map<String, List<MeetResponseDto>> findUserMeetByUserId(Long userId) {
        return queryManager.findUserMeetByUserId(userId);
    }

    public Meet saveMeet(MeetRequestDto meetRequestDto, Long userId, Long drinkId, MultipartFile multipartFile)
            throws IOException {
        validator.validateMeet(meetRequestDto, drinkId);
        imageManager.setHostAndImage(meetRequestDto, userId, multipartFile);

        ChatRoom createdChatRoom = chatManager.createChatRoom(meetRequestDto.getMeetName());
        User hostUser = validator.validateAndGetUser(userId);
        Drink drink = validator.validateAndGetDrink(drinkId);
        Tag tag = validator.validateAndGetTag(meetRequestDto.getTagId());

        Meet createdMeet = meetManager.saveMeetEntity(meetRequestDto, drink, tag, hostUser, createdChatRoom);

        meetUserService.saveMeetUser(createdMeet, hostUser, Status.HOST);
        chatManager.setupChatRoomUser(createdChatRoom, hostUser);
        pushManager.notifyFollowersAboutMeetCreation(hostUser, createdMeet);

        return createdMeet;
    }

    public void updateMeet(MeetRequestDto meetRequestDto, Long userId, Long meetId, Long drinkId,
                           MultipartFile multipartFile) throws IOException {
        validator.validateMeet(meetRequestDto, drinkId);

        String prevMeetImgSrc = meetManager.getPreviousImagePath(meetId);
        User host = validator.validateAndGetUser(userId);
        Drink drink = validator.validateAndGetDrink(drinkId);
        Tag tag = validator.validateAndGetTag(meetRequestDto.getTagId());

        imageManager.updateImage(meetRequestDto, multipartFile, prevMeetImgSrc);
        Meet updatedMeet = meetManager.updateMeet(meetId, meetRequestDto, drink, tag);

        List<MeetUser> meetUsers = meetManager.findMeetUsersByMeetId(meetId);
        pushManager.notifyMeetUpdate(host, updatedMeet, meetUsers);
    }

    @Transactional
    public void deleteMeet(Long hostId, Long meetId) {
        Meet deleteMeet = validator.validateAndGetMeet(meetId);
        validator.validateHostPermission(deleteMeet, hostId);

        List<MeetUser> meetUsers = meetManager.findMeetUsersByMeetId(meetId);

        participantManager.deleteMeetUsers(deleteMeet);
        imageManager.deleteImageIfNotDefault(deleteMeet.getImgSrc());
        meetManager.deleteMeet(meetId);

        boolean isOnlyHost = meetUsers.size() == 1 && meetUsers.get(0).getStatus().equals(Status.HOST);
        chatManager.deleteChatRoomIfEmpty(deleteMeet, isOnlyHost);

        pushManager.notifyMeetDeletion(deleteMeet, meetUsers);
    }

    public void applyMeet(Long userId, Long meetId) {
        MeetUserDto meetUserDto = queryManager.findMeetUserByMeetId(meetId);
        Meet attendMeet = validator.validateAndGetMeet(meetId);
        User attendUser = validator.validateAndGetUser(userId);
        User host = validator.validateAndGetUser(meetUserDto.getMeetRequestDto().getHostId());

        participantManager.applyMeet(userId, meetUserDto, attendMeet, attendUser);
        pushManager.notifyMeetApplication(attendUser, host, meetUserDto.getMeetRequestDto().getMeetName(),
                                          attendMeet.getMeetId());
    }

    @Transactional
    public void applyCancelMeet(Long userId, Long meetId) {
        Meet meet = validator.validateAndGetMeet(meetId);
        participantManager.applyCancelMeet(userId, meetId);
        pushManager.deletePushLog(com.ssafy.backend.common.enums.PushType.MEETACCESS, userId,
                                  meet.getHost().getUserId());
    }

    @Transactional
    public void exitMeet(Long userId, Long meetId) {
        Meet nowMeet = validator.validateAndGetMeet(meetId);
        participantManager.exitMeet(userId, meetId);
        meetManager.decreaseParticipants(nowMeet);
        chatManager.removeUserFromChatRoom(nowMeet.getChatRoom(), userId);
    }

    public String manageMeet(Long userId, Long meetId, boolean applyResult) {
        Meet managementMeet = validator.validateAndGetMeet(meetId);
        User host = managementMeet.getHost();
        User manageUser = validator.validateAndGetUser(userId);

        if (applyResult) {
            meetManager.updateParticipants(meetId);
            chatManager.addUserToChatRoom(managementMeet.getChatRoom(), manageUser, host,
                                          manageUser.getNickname());
            pushManager.notifyMeetAcceptance(host, manageUser, managementMeet.getMeetName(),
                                             managementMeet.getMeetId());
        } else {
            pushManager.notifyMeetRejection(host, manageUser, managementMeet.getMeetName());
        }

        return participantManager.manageMeet(userId, meetId, applyResult);
    }

    public void checkMeetStatus() {
        List<Meet> expiredMeets = meetManager.findExpiredMeets();
        for (Meet meet : expiredMeets) {
            if (meet.getMeetStatus() != com.ssafy.backend.common.enums.MeetStatus.END) {
                List<MeetUser> findUsers = meetManager.findMeetUsersByMeetId(meet.getMeetId());
                pushManager.notifyMeetEvaluation(meet, findUsers);
            }
        }
        meetManager.updateMeetsToEnded(expiredMeets);
    }
}