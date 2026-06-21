package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCardStatusUseCaseImplTest {

    @InjectMocks
    private UpdateCardStatusUseCaseImpl updateCardStatusUseCase;

    @Mock
    private CardRepository cardRepository;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private static final String CARD_NUMBER = "1234567890123456";

    private Card buildCard(final CardStatus status) {
        return Card.builder()
                .cardNumber(CARD_NUMBER)
                .password("encrypted")
                .balance(new BigDecimal("500.00"))
                .status(status)
                .build();
    }

    @Nested
    @DisplayName("when card does not exist")
    class WhenCardDoesNotExist {

        @Test
        @DisplayName("should throw CardNotFoundException")
        void shouldThrowCardNotFoundException() {
            when(cardRepository.findWithLockByCardNumber(CARD_NUMBER)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateCardStatusUseCase.execute(CARD_NUMBER, CardStatus.BLOCKED))
                    .isInstanceOf(CardNotFoundException.class)
                    .hasMessage("Card " + CARD_NUMBER + " not found");

            verify(cardRepository).findWithLockByCardNumber(CARD_NUMBER);
            verifyNoMoreInteractions(cardRepository);
        }
    }

    @Nested
    @DisplayName("when card exists")
    class WhenCardExists {

        @Test
        @DisplayName("should block card and save")
        void shouldBlockCard() {
            final Card activeCard = buildCard(CardStatus.ACTIVE);
            final Card blockedCard = buildCard(CardStatus.BLOCKED);

            when(cardRepository.findWithLockByCardNumber(CARD_NUMBER)).thenReturn(Optional.of(activeCard));
            when(cardRepository.save(any(Card.class))).thenReturn(blockedCard);

            final Card result = updateCardStatusUseCase.execute(CARD_NUMBER, CardStatus.BLOCKED);

            assertThat(result.status()).isEqualTo(CardStatus.BLOCKED);

            verify(cardRepository).findWithLockByCardNumber(CARD_NUMBER);
            verify(cardRepository).save(cardCaptor.capture());

            final Card saved = cardCaptor.getValue();
            assertThat(saved.status()).isEqualTo(CardStatus.BLOCKED);
            assertThat(saved.cardNumber()).isEqualTo(CARD_NUMBER);
        }

        @Test
        @DisplayName("should unblock card and save")
        void shouldUnblockCard() {
            final Card blockedCard = buildCard(CardStatus.BLOCKED);
            final Card activeCard = buildCard(CardStatus.ACTIVE);

            when(cardRepository.findWithLockByCardNumber(CARD_NUMBER)).thenReturn(Optional.of(blockedCard));
            when(cardRepository.save(any(Card.class))).thenReturn(activeCard);

            final Card result = updateCardStatusUseCase.execute(CARD_NUMBER, CardStatus.ACTIVE);

            assertThat(result.status()).isEqualTo(CardStatus.ACTIVE);

            verify(cardRepository).findWithLockByCardNumber(CARD_NUMBER);
            verify(cardRepository).save(cardCaptor.capture());

            final Card saved = cardCaptor.getValue();
            assertThat(saved.status()).isEqualTo(CardStatus.ACTIVE);
        }

        @Test
        @DisplayName("should be idempotent when status is already the same")
        void shouldBeIdempotent() {
            final Card blockedCard = buildCard(CardStatus.BLOCKED);

            when(cardRepository.findWithLockByCardNumber(CARD_NUMBER)).thenReturn(Optional.of(blockedCard));

            final Card result = updateCardStatusUseCase.execute(CARD_NUMBER, CardStatus.BLOCKED);

            assertThat(result.status()).isEqualTo(CardStatus.BLOCKED);

            verify(cardRepository).findWithLockByCardNumber(CARD_NUMBER);
            verifyNoMoreInteractions(cardRepository);
        }
    }
}
