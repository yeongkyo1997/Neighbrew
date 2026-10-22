package com.ssafy.backend.domain.meet.service.meet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.dto.MeetResponseDto;
import com.ssafy.backend.domain.meet.dto.MeetDetailResponse;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetAccepted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetApplied;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetCreated;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetDeleted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetEnded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageDiscarded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageUploaded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetRejected;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetUpdated;
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
    private final ApplicationEventPublisher eventPublisher;

    public Page<MeetResponseDto> findMeetsByTagId(Long tagId, Pageable pageable) {
        validator.validateTagId(tagId);
        return queryManager.findMeetsByTagId(tagId, pageable);
    }

    public MeetDetailResponse findMeetdetailByMeetId(Long meetId) {
        return queryManager.findMeetdetailByMeetId(meetId);
    }

    public Map<String, List<MeetResponseDto>> findUserMeetByUserId(Long userId) {
        return queryManager.findUserMeetByUserId(userId);
    }

    // 검증과 조회를 모두 끝낸 뒤에 부작용(업로드, 저장)을 시작한다.
    // 푸시 알림은 커밋 뒤에 보내고, 롤백되면 방금 올린 이미지를 지운다(MeetEventListener).
    @Transactional(rollbackFor = Exception.class)
    public Meet saveMeet(MeetRequestDto meetRequestDto, Long userId, Long drinkId, MultipartFile multipartFile)
            throws IOException {
        validator.validateMeet(meetRequestDto, drinkId);
        User hostUser = validator.validateAndGetUser(userId);
        Drink drink = validator.validateAndGetDrink(drinkId);
        Tag tag = validator.validateAndGetTag(meetRequestDto.getTagId());

        String imgSrc = imageManager.uploadImageIfExists(multipartFile);
        if (multipartFile != null) {
            eventPublisher.publishEvent(new MeetImageUploaded(imgSrc));
        }
        meetRequestDto.setHostId(userId);
        meetRequestDto.setImgSrc(imgSrc);

        ChatRoom createdChatRoom = chatManager.createChatRoom(meetRequestDto.getMeetName());
        Meet createdMeet = meetManager.saveMeetEntity(meetRequestDto, drink, tag, hostUser, createdChatRoom);
        meetUserService.saveMeetUser(createdMeet, hostUser, Status.HOST);
        // MongoDB 시스템 메시지는 JPA 트랜잭션으로 되돌릴 수 없으므로 JPA 쓰기 뒤에 둔다.
        chatManager.setupChatRoomUser(createdChatRoom, hostUser);

        eventPublisher.publishEvent(new MeetCreated(hostUser, createdMeet));
        return createdMeet;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMeet(MeetRequestDto meetRequestDto, Long userId, Long meetId, Long drinkId,
                           MultipartFile multipartFile) throws IOException {
        validator.validateMeet(meetRequestDto, drinkId);
        Meet meet = validator.validateAndGetMeet(meetId);
        validator.validateHostPermission(meet, userId);
        User host = validator.validateAndGetUser(userId);
        Drink drink = validator.validateAndGetDrink(drinkId);
        Tag tag = validator.validateAndGetTag(meetRequestDto.getTagId());

        String prevMeetImgSrc = meet.getImgSrc();
        String imgSrc = imageManager.resolveUpdatedImage(meetRequestDto.getImgSrc(), multipartFile, prevMeetImgSrc);
        if (multipartFile != null) {
            eventPublisher.publishEvent(new MeetImageUploaded(imgSrc));
        }
        if (!imgSrc.equals(prevMeetImgSrc)) {
            eventPublisher.publishEvent(new MeetImageDiscarded(prevMeetImgSrc));
        }
        meetRequestDto.setImgSrc(imgSrc);

        Meet updatedMeet = meetManager.updateMeet(meetId, meetRequestDto, drink, tag);
        List<MeetUser> meetUsers = meetManager.findMeetUsersByMeetId(meetId);
        eventPublisher.publishEvent(new MeetUpdated(host, updatedMeet, meetUsers));
    }

    @Transactional
    public void deleteMeet(Long hostId, Long meetId) {
        Meet deleteMeet = validator.validateAndGetMeet(meetId);
        validator.validateHostPermission(deleteMeet, hostId);

        List<MeetUser> meetUsers = meetManager.findMeetUsersByMeetId(meetId);

        participantManager.deleteMeetUsers(deleteMeet);
        meetManager.deleteMeet(meetId);

        boolean isOnlyHost = meetUsers.size() == 1 && meetUsers.get(0).getStatus().equals(Status.HOST);
        chatManager.deleteChatRoomIfEmpty(deleteMeet, isOnlyHost);

        // S3 이미지 삭제와 푸시는 삭제가 커밋된 뒤에 실행한다.
        eventPublisher.publishEvent(new MeetImageDiscarded(deleteMeet.getImgSrc()));
        eventPublisher.publishEvent(new MeetDeleted(deleteMeet, meetUsers));
    }

    @Transactional
    public void applyMeet(Long userId, Long meetId) {
        // 모임 정보는 참여자 목록이 아니라 Meet 엔티티에서 읽는다 (참여자가 없어도 NPE가 나지 않게)
        Meet attendMeet = validator.validateAndGetMeet(meetId);
        User attendUser = validator.validateAndGetUser(userId);

        participantManager.applyMeet(attendMeet, attendUser);
        eventPublisher.publishEvent(new MeetApplied(attendUser, attendMeet.getHost(), attendMeet.getMeetName(),
                                                    attendMeet.getMeetId()));
    }

    @Transactional
    public void applyCancelMeet(Long userId, Long meetId) {
        Meet meet = validator.validateAndGetMeet(meetId);
        participantManager.applyCancelMeet(userId, meetId);
        // 푸시 기록 삭제는 DB 작업이라 함께 롤백되므로 트랜잭션 안에서 처리한다.
        pushManager.deletePushLog(PushType.MEETACCESS, userId,
                                  meet.getHost().getUserId());
    }

    @Transactional
    public void exitMeet(Long userId, Long meetId) {
        Meet nowMeet = validator.validateAndGetMeetForUpdate(meetId);
        participantManager.exitMeet(userId, meetId);
        meetManager.decreaseParticipants(nowMeet);
        chatManager.removeUserFromChatRoom(nowMeet.getChatRoom(), userId);
    }

    // 모임 행을 잠근 채로 신청 상태 확인, 인원 증가, 상태 변경을 처리해 동시 승인으로 정원을 넘지 않게 한다.
    @Transactional
    public String manageMeet(Long hostId, Long userId, Long meetId, boolean applyResult) {
        Meet managementMeet = validator.validateAndGetMeetForUpdate(meetId);
        validator.validateHostPermission(managementMeet, hostId);
        User host = managementMeet.getHost();
        User manageUser = validator.validateAndGetUser(userId);

        if (meetUserService.findUserStatus(userId, meetId) != Status.APPLY) {
            throw new IllegalArgumentException("가입신청중인 유저만 승인/거절할 수 있습니다.");
        }

        if (applyResult) {
            meetManager.increaseParticipants(managementMeet);
        }
        String result = participantManager.manageMeet(userId, meetId, applyResult);

        if (applyResult) {
            chatManager.addUserToChatRoom(managementMeet.getChatRoom(), manageUser, host,
                                          manageUser.getNickname());
            eventPublisher.publishEvent(new MeetAccepted(host, manageUser, managementMeet.getMeetName(),
                                                         managementMeet.getMeetId()));
        } else {
            eventPublisher.publishEvent(new MeetRejected(host, manageUser, managementMeet.getMeetName()));
        }
        return result;
    }

    @Transactional
    public void checkMeetStatus() {
        List<Meet> expiredMeets = meetManager.findExpiredMeets();
        for (Meet meet : expiredMeets) {
            if (meet.getMeetStatus() != MeetStatus.END) {
                List<MeetUser> findUsers = meetManager.findMeetUsersByMeetId(meet.getMeetId());
                eventPublisher.publishEvent(new MeetEnded(meet, findUsers));
            }
        }
        meetManager.updateMeetsToEnded(expiredMeets);
    }
}