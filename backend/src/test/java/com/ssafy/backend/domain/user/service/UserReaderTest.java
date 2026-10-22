package com.ssafy.backend.domain.user.service;

import static com.ssafy.backend.support.UserFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UserReaderTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserReader userReader;

    @Test
    void 사용자가_있으면_반환한다() {
        User user = user(7L, "술꾼");
        given(userRepository.findById(7L)).willReturn(Optional.of(user));

        assertThat(userReader.getById(7L)).isSameAs(user);
    }

    @Test
    void 사용자가_없으면_404로_응답할_예외를_던진다() {
        given(userRepository.findById(7L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userReader.getById(7L)).isInstanceOf(NotFoundException.class)
                                                        .hasMessageContaining("id=7");
    }

    @Test
    void 존재_확인도_없으면_같은_예외를_던진다() {
        given(userRepository.existsById(7L)).willReturn(false);
        given(userRepository.existsById(8L)).willReturn(true);

        assertThatThrownBy(() -> userReader.validateExists(7L)).isInstanceOf(NotFoundException.class);
        assertThatCode(() -> userReader.validateExists(8L)).doesNotThrowAnyException();
    }
}
