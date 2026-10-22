package com.ssafy.backend.domain.meet.service.meet;

import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetAccepted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetCreated;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetDeleted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageDiscarded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageUploaded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetUpdated;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetChatManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetImageManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetParticipantManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetPushManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetQueryManager;
import com.ssafy.backend.domain.meet.service.meet.validator.MeetValidator;
import com.ssafy.backend.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    @Mock
    private MeetValidator validator;
    @Mock
    private MeetImageManager imageManager;
    @Mock
    private MeetQueryManager queryManager;
    @Mock
    private MeetManager meetManager;
    @Mock
    private MeetParticipantManager participantManager;
    @Mock
    private MeetPushManager pushManager;
    @Mock
    private MeetChatManager chatManager;
    @Mock
    private MeetUserService meetUserService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private MultipartFile image;

    @InjectMocks
    private MeetService meetService;

    private final User host = user(1L, "host");
    private final User guest = user(2L, "guest");

    private MeetRequestDto request() {
        return MeetRequestDto.builder().meetName("모임").maxParticipants(8).tagId(1L).minAge(20)
                             .meetDate(LocalDateTime.now().plusDays(1)).build();
    }

    private Meet meet(int now) {
        return Meet.builder().meetId(10L).meetName("모임").host(host).imgSrc("old.png").nowParticipants(now)
                   .maxParticipants(8).build();
    }

    private List<Object> publishedEvents(int count) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        then(eventPublisher).should(times(count)).publishEvent(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void 모임_생성_검증이_실패하면_업로드와_저장을_시작하지_않는다() throws Exception {
        given(validator.validateAndGetUser(1L)).willReturn(host);
        given(validator.validateAndGetTag(1L)).willThrow(new IllegalArgumentException("올바른 태그 정보가 입력되지 않았습니다."));

        assertThatThrownBy(() -> meetService.saveMeet(request(), 1L, 3L, image))
                .isInstanceOf(IllegalArgumentException.class);

        then(imageManager).shouldHaveNoInteractions();
        then(chatManager).shouldHaveNoInteractions();
        then(meetManager).shouldHaveNoInteractions();
        then(eventPublisher).shouldHaveNoInteractions();
    }

    @Test
    void 모임을_만들면_푸시는_직접_보내지_않고_커밋_후_이벤트로_넘긴다() throws Exception {
        ChatRoom chatRoom = ChatRoom.builder().build();
        Meet created = meet(1);
        given(validator.validateAndGetUser(1L)).willReturn(host);
        given(validator.validateAndGetDrink(3L)).willReturn(Drink.builder().build());
        given(validator.validateAndGetTag(1L)).willReturn(mock(Tag.class));
        given(imageManager.uploadImageIfExists(image)).willReturn("new.png");
        given(chatManager.createChatRoom("모임")).willReturn(chatRoom);
        given(meetManager.saveMeetEntity(any(), any(), any(), any(), any())).willReturn(created);

        meetService.saveMeet(request(), 1L, 3L, image);

        assertThat(publishedEvents(2)).containsExactly(new MeetImageUploaded("new.png"),
                                                       new MeetCreated(host, created));
        then(pushManager).shouldHaveNoInteractions();
    }

    @Test
    void 모임_이미지를_바꾸면_이전_이미지는_커밋_후에_지운다() throws Exception {
        Meet meet = meet(1);
        given(validator.validateAndGetMeet(10L)).willReturn(meet);
        given(validator.validateAndGetUser(1L)).willReturn(host);
        given(validator.validateAndGetDrink(3L)).willReturn(Drink.builder().build());
        given(validator.validateAndGetTag(1L)).willReturn(mock(Tag.class));
        given(imageManager.resolveUpdatedImage(any(), any(), any())).willReturn("new.png");
        given(meetManager.updateMeet(any(), any(), any(), any())).willReturn(meet);
        given(meetManager.findMeetUsersByMeetId(10L)).willReturn(List.of());

        meetService.updateMeet(request(), 1L, 10L, 3L, image);

        assertThat(publishedEvents(3)).containsExactly(new MeetImageUploaded("new.png"),
                                                       new MeetImageDiscarded("old.png"),
                                                       new MeetUpdated(host, meet, List.of()));
        then(imageManager).should(never()).deleteImageIfNotDefault(any());
    }

    @Test
    void 모임을_삭제하면_S3_삭제와_푸시를_커밋_후로_미룬다() {
        Meet meet = meet(1);
        List<MeetUser> meetUsers = List.of(MeetUser.builder().user(host).meet(meet).status(Status.HOST).build());
        given(validator.validateAndGetMeet(10L)).willReturn(meet);
        given(meetManager.findMeetUsersByMeetId(10L)).willReturn(meetUsers);

        meetService.deleteMeet(1L, 10L);

        assertThat(publishedEvents(2)).containsExactly(new MeetImageDiscarded("old.png"),
                                                       new MeetDeleted(meet, meetUsers));
        then(imageManager).shouldHaveNoInteractions();
        then(pushManager).shouldHaveNoInteractions();
    }

    @Test
    void 승인은_잠근_모임으로_인원을_늘린_뒤_상태를_바꾸고_이벤트를_발행한다() {
        Meet meet = meet(1);
        given(validator.validateAndGetMeetForUpdate(10L)).willReturn(meet);
        given(validator.validateAndGetUser(2L)).willReturn(guest);
        given(meetUserService.findUserStatus(2L, 10L)).willReturn(Status.APPLY);

        meetService.manageMeet(1L, 2L, 10L, true);

        InOrder inOrder = inOrder(meetManager, participantManager);
        inOrder.verify(meetManager).increaseParticipants(meet);
        inOrder.verify(participantManager).manageMeet(2L, 10L, true);
        then(validator).should(never()).validateAndGetMeet(any());
        assertThat(publishedEvents(1)).containsExactly(new MeetAccepted(host, guest, "모임", 10L));
        then(pushManager).shouldHaveNoInteractions();
    }

    @Test
    void 정원이_가득_차면_승인하지_않고_알림도_보내지_않는다() {
        Meet meet = meet(8);
        given(validator.validateAndGetMeetForUpdate(10L)).willReturn(meet);
        given(validator.validateAndGetUser(2L)).willReturn(guest);
        given(meetUserService.findUserStatus(2L, 10L)).willReturn(Status.APPLY);
        willThrow(new IllegalArgumentException("해당 모임에 참여 인원이 가득 찼습니다.")).given(meetManager)
                                                                           .increaseParticipants(meet);

        assertThatThrownBy(() -> meetService.manageMeet(1L, 2L, 10L, true))
                .isInstanceOf(IllegalArgumentException.class);

        then(participantManager).should(never()).manageMeet(any(), any(), anyBoolean());
        then(chatManager).shouldHaveNoInteractions();
        then(eventPublisher).shouldHaveNoInteractions();
    }

    @Test
    void 모임_나가기는_잠근_모임으로_인원을_줄인다() {
        Meet meet = meet(2);
        given(validator.validateAndGetMeetForUpdate(10L)).willReturn(meet);

        meetService.exitMeet(2L, 10L);

        then(meetManager).should().decreaseParticipants(meet);
        then(validator).should(never()).validateAndGetMeet(any());
    }
}
