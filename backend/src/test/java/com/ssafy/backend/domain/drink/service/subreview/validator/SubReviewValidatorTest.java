package com.ssafy.backend.domain.drink.service.subreview.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.drink.repository.DrinkReviewRepository;
import com.ssafy.backend.domain.drink.repository.SubReviewRepository;
import com.ssafy.backend.domain.user.service.UserReader;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubReviewValidatorTest {
    @Mock
    private UserReader userReader;
    @Mock
    private DrinkReviewRepository drinkReviewRepository;
    @Mock
    private SubReviewRepository subReviewRepository;

    @InjectMocks
    private SubReviewValidator subReviewValidator;

    @Test
    void 없는_후기나_댓글은_404로_응답할_예외를_던진다() {
        given(drinkReviewRepository.findById(3L)).willReturn(Optional.empty());
        given(subReviewRepository.findById(4L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> subReviewValidator.validateAndGetDrinkReview(3L))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> subReviewValidator.validateAndGetSubReview(4L))
                .isInstanceOf(NotFoundException.class);
    }
}
