package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceValidationHandlerTest {

    @Nested
    @DisplayName("when balance is sufficient")
    class WhenBalanceIsSufficient {

        @Test
        @DisplayName("should set CONTINUE status")
        void shouldSetContinueStatus() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("50.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.CONTINUE);
            assertThat(context.getException()).isNull();
        }
    }

    @Nested
    @DisplayName("when balance is exactly equal to amount")
    class WhenBalanceIsEqualToAmount {

        @Test
        @DisplayName("should set CONTINUE status")
        void shouldSetContinueStatus() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("30.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.CONTINUE);
            assertThat(context.getException()).isNull();
        }
    }

    @Nested
    @DisplayName("when balance is insufficient")
    class WhenBalanceIsInsufficient {

        @Test
        @DisplayName("should set STOP status and exception")
        void shouldSetStopStatusAndException() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("10.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            assertThat(context.getStatus()).isEqualTo(HandlerStatus.STOP);
            assertThat(context.getException()).isInstanceOf(InsufficientBalanceException.class);
            assertThat(context.getException()).hasMessage("SALDO_INSUFICIENTE");
        }
    }
}
