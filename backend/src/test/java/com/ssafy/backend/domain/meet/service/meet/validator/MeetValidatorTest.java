package com.ssafy.backend.domain.meet.service.meet.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.common.repository.TagRepository;
import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.repository.MeetRepository;
import com.ssafy.backend.domain.user.service.UserReader;

@ExtendWith(MockitoExtension.class)
class MeetValidatorTest {
    @Mock
    private UserReader userReader;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private DrinkRepository drinkRepository;
    @Mock
    private MeetRepository meetRepository;

    @InjectMocks
    private MeetValidator meetValidator;

    private MeetRequestDto request(Integer minAge) {
        return MeetRequestDto.builder().meetName("모임").maxParticipants(8).tagId(1L).minAge(minAge)
                             .meetDate(LocalDateTime.now().plusDays(1)).build();
    }

    // 예전에는 최소 나이를 비워 두면 getMinAge() < 20 비교에서 NPE(500)가 났다
    @Test
    void 최소_나이를_비워_두어도_모임을_만들_수_있다() {
        assertThatCode(() -> meetValidator.validateMeet(request(null), 3L)).doesNotThrowAnyException();
    }

    @Test
    void 최소_나이를_입력했다면_20세_이상이어야_한다() {
        assertThatThrownBy(() -> meetValidator.validateMeet(request(19), 3L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> meetValidator.validateMeet(request(20), 3L)).doesNotThrowAnyException();
    }
}
