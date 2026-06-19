package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceValidationHandlerTest {

    @Nested
    @DisplayName("when balance is sufficient")
    class WhenBalanceIsSufficient {

        @Test
        @DisplayName("should not throw exception")
        void shouldNotThrow() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("50.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            assertThatCode(() -> handler.doHandle(context))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("when balance is exactly equal to amount")
    class WhenBalanceIsEqualToAmount {

        @Test
        @DisplayName("should not throw exception")
        void shouldNotThrow() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("30.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            assertThatCode(() -> handler.doHandle(context))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("when balance is insufficient")
    class WhenBalanceIsInsufficient {

        @Test
        @DisplayName("should throw InsufficientBalanceException")
        void shouldThrow() {
            final Card card = Card.builder()
                    .cardNumber("123")
                    .balance(new BigDecimal("10.00"))
                    .build();
            final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));

            final BalanceValidationHandler handler = new BalanceValidationHandler();
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            assertThatThrownBy(() -> handler.doHandle(context))
                    .isInstanceOf(InsufficientBalanceException.class)
                    .hasMessage("SALDO_INSUFICIENTE");
        }
    }
}
