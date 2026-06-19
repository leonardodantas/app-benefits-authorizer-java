package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.InvalidPasswordException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordValidationHandlerTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private final Card card = Card.builder()
            .cardNumber("123")
            .password("encrypted-password")
            .balance(new BigDecimal("100"))
            .build();

    @Nested
    @DisplayName("when password matches")
    class WhenPasswordMatches {

        @Test
        @DisplayName("should not throw exception")
        void shouldNotThrow() {
            final Transaction transaction = new Transaction("123", "raw-password", BigDecimal.TEN);
            when(passwordEncoder.matches("raw-password", "encrypted-password")).thenReturn(true);

            final PasswordValidationHandler handler = new PasswordValidationHandler(passwordEncoder);
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            handler.doHandle(context);

            verify(passwordEncoder).matches("raw-password", "encrypted-password");
            verifyNoMoreInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("when password does not match")
    class WhenPasswordDoesNotMatch {

        @Test
        @DisplayName("should throw InvalidPasswordException")
        void shouldThrow() {
            final Transaction transaction = new Transaction("123", "wrong-password", BigDecimal.TEN);
            when(passwordEncoder.matches("wrong-password", "encrypted-password")).thenReturn(false);

            final PasswordValidationHandler handler = new PasswordValidationHandler(passwordEncoder);
            final TransactionContext context = new TransactionContext(transaction);
            context.setCard(card);

            assertThatThrownBy(() -> handler.doHandle(context))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessage("SENHA_INVALIDA");

            verify(passwordEncoder).matches("wrong-password", "encrypted-password");
            verifyNoMoreInteractions(passwordEncoder);
        }
    }
}
