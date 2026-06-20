package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardExistenceHandlerTest {

    @Mock
    private CardRepository cardRepository;

    private final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);

    @Nested
    @DisplayName("when card exists")
    class WhenCardExists {

        @Test
        @DisplayName("should set card in context")
        void shouldSetCardInContext() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .password("encrypted")
                    .balance(new BigDecimal("100"))
                    .build();

            when(cardRepository.findWithLockByCardNumber("123")).thenReturn(Optional.of(card));

            final CardExistenceHandler handler = new CardExistenceHandler(cardRepository);
            final TransactionContext context = new TransactionContext(transaction);

            handler.doHandle(context);

            assertThat(context.getCard()).isEqualTo(card);
            verify(cardRepository).findWithLockByCardNumber("123");
            verifyNoMoreInteractions(cardRepository);
        }
    }

    @Nested
    @DisplayName("when card does not exist")
    class WhenCardDoesNotExist {

        @Test
        @DisplayName("should set STOP status and exception")
        void shouldSetStopStatusAndException() {
            when(cardRepository.findWithLockByCardNumber("123")).thenReturn(Optional.empty());

            final CardExistenceHandler handler = new CardExistenceHandler(cardRepository);
            final TransactionContext context = new TransactionContext(transaction);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.STOP);
            assertThat(context.getException()).isInstanceOf(CardNotExistsException.class);
            assertThat(context.getException()).hasMessage("CARTAO_INEXISTENTE");
            verify(cardRepository).findWithLockByCardNumber("123");
            verifyNoMoreInteractions(cardRepository);
        }
    }
}
