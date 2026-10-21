package com.ssafy.backend.domain.evaluation.service.evaluation.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.evaluation.repository.EvaluationRepository;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class EvaluationValidatorTest {
    private static final Long MEET_ID = 10L;
    private static final Long EVALUATOR_ID = 1L;
    private static final Long TARGET_ID = 2L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MeetRepository meetRepository;
    @Mock
    private MeetUserRepository meetUserRepository;
    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private EvaluationValidator evaluationValidator;

    @Test
    void 모임에_참여하지_않은_평가자는_거부한다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(null);

        assertThatThrownBy(() -> evaluationValidator.validateParticipants(MEET_ID, EVALUATOR_ID, TARGET_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 모임에_참여하지_않은_대상은_평가할_수_없다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.GUEST);
        given(meetUserRepository.findStatusByUserIdAndMeetId(TARGET_ID, MEET_ID)).willReturn(null);

        assertThatThrownBy(() -> evaluationValidator.validateParticipants(MEET_ID, EVALUATOR_ID, TARGET_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 모임_신청만_한_사용자는_평가할_수_없다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.APPLY);

        assertThatThrownBy(() -> evaluationValidator.validateParticipants(MEET_ID, EVALUATOR_ID, TARGET_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 모임_신청만_한_대상은_평가할_수_없다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.HOST);
        given(meetUserRepository.findStatusByUserIdAndMeetId(TARGET_ID, MEET_ID)).willReturn(Status.APPLY);

        assertThatThrownBy(() -> evaluationValidator.validateParticipants(MEET_ID, EVALUATOR_ID, TARGET_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 모임_참여자끼리는_평가할_수_있다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.HOST);
        given(meetUserRepository.findStatusByUserIdAndMeetId(TARGET_ID, MEET_ID)).willReturn(Status.GUEST);

        assertThatCode(() -> evaluationValidator.validateParticipants(MEET_ID, EVALUATOR_ID, TARGET_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void 진행_중인_모임은_평가할_수_없다() {
        Meet meet = Meet.builder().meetStatus(MeetStatus.WAITING).build();

        assertThatThrownBy(() -> evaluationValidator.validateMeetEnded(meet))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상태가_없는_모임은_평가할_수_없다() {
        Meet meet = Meet.builder().build();

        assertThatThrownBy(() -> evaluationValidator.validateMeetEnded(meet))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 종료된_모임은_평가할_수_있다() {
        Meet meet = Meet.builder().meetStatus(MeetStatus.END).build();

        assertThatCode(() -> evaluationValidator.validateMeetEnded(meet)).doesNotThrowAnyException();
    }
}
