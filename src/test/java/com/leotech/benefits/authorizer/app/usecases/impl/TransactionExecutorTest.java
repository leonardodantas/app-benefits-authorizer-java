package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionExecutorTest {

    @Mock
    private TransactionHandler chain;

    @Test
    @DisplayName("should return card when chain succeeds")
    void shouldReturnCardOnSuccess() {
        final Card card = Card.builder()
                .cardNumber("1234567890123456")
                .balance(new BigDecimal("100.00"))
                .build();

        doAnswer(invocation -> {
            final TransactionContext ctx = invocation.getArgument(0);
            ctx.setCard(card);
            ctx.setStatus(HandlerStatus.SUCCESS);
            return null;
        }).when(chain).handle(any(TransactionContext.class));

        final var executor = new TransactionExecutor(chain);
        final var transaction = new Transaction("1234567890123456", "1234", BigDecimal.TEN);

        final Card result = executor.execute(transaction);

        assertThat(result).isEqualTo(card);
        verify(chain).handle(any(TransactionContext.class));
        verifyNoMoreInteractions(chain);
    }

    @Test
    @DisplayName("should throw exception when chain stops with error")
    void shouldThrowOnStopWithException() {
        doAnswer(invocation -> {
            final TransactionContext ctx = invocation.getArgument(0);
            ctx.setStatus(HandlerStatus.STOP);
            ctx.setException(new CardNotExistsException());
            return null;
        }).when(chain).handle(any(TransactionContext.class));

        final var executor = new TransactionExecutor(chain);
        final var transaction = new Transaction("1234567890123456", "1234", BigDecimal.TEN);

        assertThatThrownBy(() -> executor.execute(transaction))
                .isInstanceOf(CardNotExistsException.class)
                .hasMessage("CARTAO_INEXISTENTE");

        verify(chain).handle(any(TransactionContext.class));
        verifyNoMoreInteractions(chain);
    }

    @Test
    @DisplayName("should return card when chain stops without exception")
    void shouldReturnCardOnStopWithoutException() {
        final Card card = Card.builder()
                .cardNumber("1234567890123456")
                .balance(new BigDecimal("100.00"))
                .build();

        doAnswer(invocation -> {
            final TransactionContext ctx = invocation.getArgument(0);
            ctx.setCard(card);
            ctx.setStatus(HandlerStatus.STOP);
            return null;
        }).when(chain).handle(any(TransactionContext.class));

        final var executor = new TransactionExecutor(chain);
        final var transaction = new Transaction("1234567890123456", "1234", BigDecimal.TEN);

        final Card result = executor.execute(transaction);

        assertThat(result).isEqualTo(card);
        verify(chain).handle(any(TransactionContext.class));
        verifyNoMoreInteractions(chain);
    }
}
