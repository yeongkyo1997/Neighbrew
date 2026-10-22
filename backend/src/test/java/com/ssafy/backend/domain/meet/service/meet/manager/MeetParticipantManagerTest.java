package com.ssafy.backend.domain.meet.service.meet.manager;

import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.service.MeetUserService;
import com.ssafy.backend.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class MeetParticipantManagerTest {
    @Mock
    private MeetUserService meetUserService;

    @InjectMocks
    private MeetParticipantManager participantManager;

    private final User applicant = user(9L, "신청자");

    private Meet meet(int now, int max) {
        return Meet.builder().meetId(10L).nowParticipants(now).maxParticipants(max).build();
    }

    // 예전에는 첫 번째 참여자에서 모임 정보를 꺼내서 참여자가 없으면 NPE가 났다
    @Test
    void 참여자_목록이_비어_있어도_신청할_수_있다() {
        Meet meet = meet(1, 8);
        given(meetUserService.findUserStatus(9L, 10L)).willReturn(null);

        participantManager.applyMeet(meet, applicant);

        then(meetUserService).should().saveMeetUser(meet, applicant, Status.APPLY);
    }

    @Test
    void 이미_신청했거나_참여한_사용자는_다시_신청할_수_없다() {
        Meet meet = meet(1, 8);
        given(meetUserService.findUserStatus(9L, 10L)).willReturn(Status.APPLY);

        assertThatThrownBy(() -> participantManager.applyMeet(meet, applicant))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("이미 참여");
        then(meetUserService).should(never()).saveMeetUser(any(), any(), any());
    }

    @Test
    void 정원이_찬_모임에는_신청할_수_없다() {
        Meet meet = meet(8, 8);

        assertThatThrownBy(() -> participantManager.applyMeet(meet, applicant))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("가득");
        then(meetUserService).should(never()).saveMeetUser(any(), any(), any());
    }

    @Test
    void 간수치_제한에_맞지_않으면_신청을_저장하지_않는다() {
        Meet meet = Meet.builder().meetId(10L).nowParticipants(1).maxParticipants(8).minLiverPoint(90f).build();

        assertThatThrownBy(() -> participantManager.applyMeet(meet, applicant))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("간수치");
        then(meetUserService).should(never()).saveMeetUser(any(), any(), any());
    }
}
