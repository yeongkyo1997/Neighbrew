package com.ssafy.backend.domain.drink.service.drink.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.drink.repository.DrinkRepository;
import com.ssafy.backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class DrinkValidatorTest {
    @Mock
    private DrinkRepository drinkRepository;

    @InjectMocks
    private DrinkValidator drinkValidator;

    @Test
    void 없는_술은_404로_응답할_예외를_던진다() {
        given(drinkRepository.findById(5L)).willReturn(Optional.empty());
        given(drinkRepository.existsById(5L)).willReturn(false);

        assertThatThrownBy(() -> drinkValidator.validateAndGetDrink(5L)).isInstanceOf(NotFoundException.class)
                                                                          .hasMessageContaining("id=5");
        assertThatThrownBy(() -> drinkValidator.validateDrinkExists(5L)).isInstanceOf(NotFoundException.class);
    }
}
