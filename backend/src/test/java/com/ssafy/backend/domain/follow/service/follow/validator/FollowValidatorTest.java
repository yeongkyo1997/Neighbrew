package com.ssafy.backend.domain.follow.service.follow.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.domain.user.service.UserReader;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class FollowValidatorTest {
    @Mock
    private UserRepository userRepository;

    private FollowValidator followValidator;

    @BeforeEach
    void setUp() {
        followValidator = new FollowValidator(new UserReader(userRepository));
    }

    // 예전에는 AssertionError(Error)를 던져 전역 예외 처리기를 거치지 않고 500이 났다
    @Test
    void 없는_사용자를_조회하면_404로_응답할_예외를_던진다() {
        given(userRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> followValidator.validateUserExists(99L)).isInstanceOf(NotFoundException.class);
    }
}
