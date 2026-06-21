package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardBlockedException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CardBlockedHandlerTest {

    private final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);
    private final CardBlockedHandler handler = new CardBlockedHandler();

    @Nested
    @DisplayName("when card is active")
    class WhenCardIsActive {

        @Test
        @DisplayName("should set CONTINUE status")
        void shouldSetContinueStatus() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .status(CardStatus.ACTIVE)
                    .build();

            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.CONTINUE);
        }
    }

    @Nested
    @DisplayName("when card is blocked")
    class WhenCardIsBlocked {

        @Test
        @DisplayName("should set STOP status and CardBlockedException")
        void shouldSetStopStatusAndException() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .status(CardStatus.BLOCKED)
                    .build();

            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.STOP);
            assertThat(context.getException()).isInstanceOf(CardBlockedException.class);
            assertThat(context.getException()).hasMessage("CARTAO_BLOQUEADO");
        }
    }
}
