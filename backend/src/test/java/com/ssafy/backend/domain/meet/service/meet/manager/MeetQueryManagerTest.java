package com.ssafy.backend.domain.meet.service.meet.manager;

import static com.ssafy.backend.support.UserFixture.assertPublicUser;
import static com.ssafy.backend.support.UserFixture.emailNicknameUser;
import static com.ssafy.backend.support.UserFixture.toJson;
import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetDetailResponse;
import com.ssafy.backend.domain.meet.dto.MeetDetailResponse.Participant;
import com.ssafy.backend.domain.meet.dto.MeetResponseDto;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.repository.GugunRepository;
import com.ssafy.backend.domain.region.repository.SidoRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class MeetQueryManagerTest {
    @Mock
    private MeetRepository meetRepository;
    @Mock
    private MeetUserRepository meetUserRepository;
    @Mock
    private SidoRepository sidoRepository;
    @Mock
    private GugunRepository gugunRepository;

    private MeetQueryManager meetQueryManager;

    private final User host = emailNicknameUser(7L);
    private Meet meet;

    @BeforeEach
    void setUp() {
        // 운영과 같은 기본 설정의 ModelMapper로 Meet -> MeetResponseDto 변환까지 확인한다.
        meetQueryManager = new MeetQueryManager(meetRepository, meetUserRepository, sidoRepository, gugunRepository,
                                                new ModelMapper());
        meet = Meet.builder().meetId(1L).meetName("모임").host(host).nowParticipants(1).maxParticipants(4)
                   .tag(new Tag()).sidoCode(11).gugunCode(110).drink(Drink.builder().drinkId(2L).name("막걸리").build())
                   .chatRoom(ChatRoom.builder().chatRoomId(3L).build()).meetStatus(MeetStatus.WAITING).build();
    }

    private void givenRegion() {
        given(sidoRepository.findById(11)).willReturn(Optional.of(mock(Sido.class)));
        given(gugunRepository.findBySidoCodeAndGugunCode(11, 110)).willReturn(
                Optional.of(Gugun.builder().sidoCode(11).gugunCode(110).gugunName("종로구").build()));
    }

    @Test
    void 모임_목록의_방장은_공개_프로필만_담는다() {
        givenRegion();
        given(meetRepository.findByMeetStatusNotOrderByCreatedAtDesc(eq(MeetStatus.END), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(meet)));

        Page<MeetResponseDto> meets = meetQueryManager.findMeetsByTagId(0L, PageRequest.of(0, 10));

        assertPublicUser(toJson(meets.getContent().get(0)).get("host"), 7L, "user_7");
    }

    @Test
    void 모임_상세의_참여자와_방장은_공개_프로필만_담는다() {
        givenRegion();
        given(meetRepository.findById(1L)).willReturn(Optional.of(meet));
        given(meetUserRepository.findByMeetMeetIdOrderByStatusDesc(1L)).willReturn(
                List.of(MeetUser.builder().user(host).meet(meet).status(Status.HOST).build(),
                        MeetUser.builder().user(user(8L, "참여자")).meet(meet).status(Status.GUEST).build()));

        MeetDetailResponse detail = meetQueryManager.findMeetdetailByMeetId(1L);

        JsonNode json = toJson(detail);
        assertPublicUser(json.get("participants").get(0).get("user"), 7L, "user_7");
        assertPublicUser(json.get("participants").get(1).get("user"), 8L, "참여자");
        assertPublicUser(json.get("meet").get("host"), 7L, "user_7");
    }

    @Test
    void 모임_상세는_참여자와_상태를_한_객체로_짝지어_담는다() {
        givenRegion();
        given(meetRepository.findById(1L)).willReturn(Optional.of(meet));
        given(meetUserRepository.findByMeetMeetIdOrderByStatusDesc(1L)).willReturn(
                List.of(MeetUser.builder().user(host).meet(meet).status(Status.HOST).build(),
                        MeetUser.builder().user(user(9L, "신청자")).meet(meet).status(Status.APPLY).build()));

        MeetDetailResponse detail = meetQueryManager.findMeetdetailByMeetId(1L);

        assertThat(detail.participants()).extracting(p -> p.user().getUserId(), Participant::status)
                                         .containsExactly(tuple(7L, Status.HOST), tuple(9L, Status.APPLY));
        assertThat(toJson(detail).get("participants").get(1).get("status").asText()).isEqualTo("APPLY");
    }

    @Test
    void 없는_모임의_상세는_404로_응답할_예외를_던진다() {
        given(meetRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> meetQueryManager.findMeetdetailByMeetId(1L)).isInstanceOf(NotFoundException.class);
    }
}
