package com.ssafy.backend.domain.evaluation.service.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.common.enums.EvaluationType;
import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.entity.Evaluation;
import com.ssafy.backend.domain.evaluation.repository.EvaluationRepository;
import com.ssafy.backend.domain.evaluation.service.evaluation.manager.EvaluationManager;
import com.ssafy.backend.domain.evaluation.service.evaluation.manager.EvaluationScoreManager;
import com.ssafy.backend.domain.evaluation.service.evaluation.validator.EvaluationValidator;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.meet.repository.MeetUserRepository;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {
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

    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        EvaluationValidator validator = new EvaluationValidator(userRepository, meetRepository, meetUserRepository,
                                                                evaluationRepository);
        EvaluationManager evaluationManager = new EvaluationManager(userRepository, evaluationRepository);
        evaluationService = new EvaluationService(validator, new EvaluationScoreManager(), evaluationManager);
    }

    @Test
    void 모임_참여자끼리의_평가는_대상_점수에_반영된다() {
        User evaluator = User.builder().userId(EVALUATOR_ID).liverPoint(40.0f).build();
        User target = User.builder().userId(TARGET_ID).liverPoint(40.0f).build();
        Meet meet = Meet.builder().meetId(MEET_ID).meetStatus(MeetStatus.END).build();

        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.GUEST);
        given(meetUserRepository.findStatusByUserIdAndMeetId(TARGET_ID, MEET_ID)).willReturn(Status.HOST);
        given(userRepository.findById(EVALUATOR_ID)).willReturn(Optional.of(evaluator));
        given(userRepository.findById(TARGET_ID)).willReturn(Optional.of(target));
        given(meetRepository.findById(MEET_ID)).willReturn(Optional.of(meet));
        given(meetUserRepository.countByMeetMeetId(MEET_ID)).willReturn(Optional.of(3L));

        String result = evaluationService.calculateScoreByMeetId(request("GOOD"), EVALUATOR_ID);

        assertThat(result).isEqualTo("성공적으로 반영되었습니다.");
        assertThat(target.getLiverPoint()).isEqualTo(40.0f + 1.0f / 3L);
        assertThat(evaluator.getLiverPoint()).isEqualTo(40.0f);
        then(evaluationRepository).should().existsByRatedUserAndReviewerAndMeet(evaluator, target, meet);

        ArgumentCaptor<Evaluation> captor = ArgumentCaptor.forClass(Evaluation.class);
        then(evaluationRepository).should().save(captor.capture());
        Evaluation saved = captor.getValue();
        assertThat(saved.getRatedUser()).isSameAs(evaluator);
        assertThat(saved.getReviewer()).isSameAs(target);
        assertThat(saved.getMeet()).isSameAs(meet);
        assertThat(saved.getEvaluationType()).isEqualTo(EvaluationType.GOOD);
        then(userRepository).should().save(target);
    }

    @Test
    void 모임과_무관한_사용자의_평가는_거부되고_아무것도_저장되지_않는다() {
        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(null);

        assertThatThrownBy(() -> evaluationService.calculateScoreByMeetId(request("BAD"), EVALUATOR_ID))
                .isInstanceOf(ForbiddenException.class);

        then(userRepository).should(never()).save(any());
        then(evaluationRepository).should(never()).save(any());
    }

    @Test
    void 종료되지_않은_모임에서는_평가가_거부된다() {
        User evaluator = User.builder().userId(EVALUATOR_ID).liverPoint(40.0f).build();
        User target = User.builder().userId(TARGET_ID).liverPoint(40.0f).build();
        Meet meet = Meet.builder().meetId(MEET_ID).meetStatus(MeetStatus.WAITING).build();

        given(meetUserRepository.findStatusByUserIdAndMeetId(EVALUATOR_ID, MEET_ID)).willReturn(Status.HOST);
        given(meetUserRepository.findStatusByUserIdAndMeetId(TARGET_ID, MEET_ID)).willReturn(Status.GUEST);
        given(userRepository.findById(EVALUATOR_ID)).willReturn(Optional.of(evaluator));
        given(userRepository.findById(TARGET_ID)).willReturn(Optional.of(target));
        given(meetRepository.findById(MEET_ID)).willReturn(Optional.of(meet));

        assertThatThrownBy(() -> evaluationService.calculateScoreByMeetId(request("BAD"), EVALUATOR_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료된 모임만 평가할 수 있습니다.");

        assertThat(target.getLiverPoint()).isEqualTo(40.0f);
        then(meetUserRepository).should(never()).countByMeetMeetId(any());
        then(evaluationRepository).should(never()).save(any());
    }

    private EvaluationRequestDto request(String evaluationType) {
        return EvaluationRequestDto.builder()
                                   .ratedUser(EVALUATOR_ID)
                                   .reviewer(TARGET_ID)
                                   .meetId(MEET_ID)
                                   .evaluationType(evaluationType)
                                   .build();
    }
}
