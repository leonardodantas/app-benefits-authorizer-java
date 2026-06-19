package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBalanceUseCaseImplTest {

    @InjectMocks
    private GetBalanceUseCaseImpl getBalanceUseCase;

    @Mock
    private CardRepository cardRepository;

    private static final String CARD_NUMBER = "123456789";
    private static final BigDecimal BALANCE = new BigDecimal("495.15");

    @Nested
    @DisplayName("when card exists")
    class WhenCardExists {

        @Test
        @DisplayName("should return the balance")
        void shouldReturnBalance() {
            final Card card = Card.builder()
                    .cardNumber(CARD_NUMBER)
                    .password("1234")
                    .balance(BALANCE)
                    .build();

            when(cardRepository.findByCardNumber(CARD_NUMBER))
                    .thenReturn(Optional.of(card));

            final BigDecimal result = getBalanceUseCase.execute(CARD_NUMBER);

            assertThat(result).isEqualByComparingTo(BALANCE);

            verify(cardRepository).findByCardNumber(CARD_NUMBER);
            verifyNoMoreInteractions(cardRepository);
        }
    }

    @Nested
    @DisplayName("when card does not exist")
    class WhenCardDoesNotExist {

        @Test
        @DisplayName("should throw CardNotFoundException")
        void shouldThrowCardNotFoundException() {
            when(cardRepository.findByCardNumber(CARD_NUMBER))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> getBalanceUseCase.execute(CARD_NUMBER))
                    .isInstanceOf(CardNotFoundException.class)
                    .hasMessage("Card " + CARD_NUMBER + " not found");

            verify(cardRepository).findByCardNumber(CARD_NUMBER);
            verifyNoMoreInteractions(cardRepository);
        }
    }
}
