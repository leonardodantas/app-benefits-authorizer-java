package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.config.AppProperties;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCardUseCaseImplTest {

    @InjectMocks
    private CreateCardUseCaseImpl createCardUseCase;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppProperties appProperties;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private static final String CARD_NUMBER = "123456789";
    private static final String PASSWORD = "1234";
    private static final String ENCRYPTED_PASSWORD = "encrypted-1234";
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("500.00");

    @Nested
    @DisplayName("when card already exists")
    class WhenCardAlreadyExists {

        @Test
        @DisplayName("should throw CardAlreadyExistsException")
        void shouldThrowCardAlreadyExistsException() {
            final Card input = Card.builder()
                    .cardNumber(CARD_NUMBER)
                    .password(PASSWORD)
                    .build();

            when(cardRepository.findByCardNumber(CARD_NUMBER))
                    .thenReturn(Optional.of(input));

            assertThatThrownBy(() -> createCardUseCase.execute(input))
                    .isInstanceOf(CardAlreadyExistsException.class)
                    .hasMessage("Card " + CARD_NUMBER + " already exists");

            verify(cardRepository).findByCardNumber(CARD_NUMBER);
            verifyNoMoreInteractions(cardRepository);
            verifyNoInteractions(passwordEncoder, appProperties);
        }
    }

    @Nested
    @DisplayName("when card is new")
    class WhenCardIsNew {

        @Test
        @DisplayName("should encrypt password, set balance, save and return the card")
        void shouldCreateCardSuccessfully() {
            final Card input = Card.builder()
                    .cardNumber(CARD_NUMBER)
                    .password(PASSWORD)
                    .build();

            final Card savedCard = Card.builder()
                    .cardNumber(CARD_NUMBER)
                    .password(ENCRYPTED_PASSWORD)
                    .balance(INITIAL_BALANCE)
                    .status(CardStatus.ACTIVE)
                    .build();

            when(cardRepository.findByCardNumber(CARD_NUMBER))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(ENCRYPTED_PASSWORD);
            when(appProperties.initialBalance())
                    .thenReturn(INITIAL_BALANCE);
            when(cardRepository.save(any(Card.class)))
                    .thenReturn(savedCard);

            final Card result = createCardUseCase.execute(input);

            assertThat(result).isEqualTo(savedCard);

            verify(cardRepository).findByCardNumber(CARD_NUMBER);
            verify(passwordEncoder).encode(PASSWORD);
            verify(appProperties).initialBalance();
            verify(cardRepository).save(cardCaptor.capture());

            final Card captured = cardCaptor.getValue();
            assertThat(captured.cardNumber()).isEqualTo(CARD_NUMBER);
            assertThat(captured.password()).isEqualTo(ENCRYPTED_PASSWORD);
            assertThat(captured.balance()).isEqualByComparingTo(INITIAL_BALANCE);
            assertThat(captured.status()).isEqualTo(CardStatus.ACTIVE);

            verifyNoMoreInteractions(cardRepository, passwordEncoder, appProperties);
        }
    }
}
