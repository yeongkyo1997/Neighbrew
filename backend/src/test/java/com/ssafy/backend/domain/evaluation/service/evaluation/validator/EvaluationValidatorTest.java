package com.ssafy.backend.domain.evaluation.service.evaluation.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

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
import com.ssafy.backend.domain.user.service.UserReader;
import com.ssafy.backend.exception.ForbiddenException;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class EvaluationValidatorTest {
    private static final Long MEET_ID = 10L;
    private static final Long EVALUATOR_ID = 1L;
    private static final Long TARGET_ID = 2L;

    @Mock
    private UserReader userReader;
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

    @Test
    void 평가_대상_사용자가_없으면_500이_아니라_404로_응답할_예외를_던진다() {
        given(userReader.getById(TARGET_ID)).willThrow(new NotFoundException("사용자를 찾을 수 없습니다. id=" + TARGET_ID));

        assertThatThrownBy(() -> evaluationValidator.validateAndGetTarget(TARGET_ID))
                .isInstanceOf(NotFoundException.class);
    }

    // 예전에는 IllegalStateException이라 500으로 응답했다
    @Test
    void 평가할_모임이_없으면_404로_응답할_예외를_던진다() {
        given(meetRepository.findById(MEET_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationValidator.validateAndGetMeet(MEET_ID))
                .isInstanceOf(NotFoundException.class);
    }
}
