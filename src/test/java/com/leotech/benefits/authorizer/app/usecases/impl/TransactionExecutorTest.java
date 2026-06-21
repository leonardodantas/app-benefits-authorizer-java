package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionExecutorTest {

    @Mock
    private TransactionHandler chain;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<TransactionEvent> eventCaptor;

    @Test
    @DisplayName("should return card and publish success event when chain succeeds")
    void shouldReturnCardOnSuccess() {
        final Card card = Card.builder()
                .cardNumber("1234567890123456")
                .balance(new BigDecimal("70.00"))
                .build();

        doAnswer(invocation -> {
            final TransactionContext ctx = invocation.getArgument(0);
            ctx.setCard(card);
            ctx.setStatus(HandlerStatus.SUCCESS);
            return null;
        }).when(chain).handle(any(TransactionContext.class));

        final TransactionExecutor executor = new TransactionExecutor(chain, eventPublisher);
        final Transaction transaction = new Transaction("1234567890123456", "1234", new BigDecimal("30.00"));

        final Card result = executor.execute(transaction);

        assertThat(result).isEqualTo(card);
        verify(chain).handle(any(TransactionContext.class));
        verify(eventPublisher).publishEvent(any(TransactionEvent.class));
        verifyNoMoreInteractions(chain, eventPublisher);
    }

    @Test
    @DisplayName("should throw exception and publish error event when chain stops with error")
    void shouldThrowOnStopWithException() {
        doAnswer(invocation -> {
            final TransactionContext ctx = invocation.getArgument(0);
            ctx.setStatus(HandlerStatus.STOP);
            ctx.setException(new CardNotExistsException());
            return null;
        }).when(chain).handle(any(TransactionContext.class));

        final TransactionExecutor executor = new TransactionExecutor(chain, eventPublisher);
        final Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.TEN);

        assertThatThrownBy(() -> executor.execute(transaction))
                .isInstanceOf(CardNotExistsException.class)
                .hasMessage("CARTAO_INEXISTENTE");

        verify(chain).handle(any(TransactionContext.class));
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        final TransactionEvent event = eventCaptor.getValue();
        assertThat(event.status()).isEqualTo(com.leotech.benefits.authorizer.domain.transaction.TransactionStatus.ERROR);
        assertThat(event.message()).isEqualTo("CARTAO_INEXISTENTE");
        verifyNoMoreInteractions(chain, eventPublisher);
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

        final TransactionExecutor executor = new TransactionExecutor(chain, eventPublisher);
        final Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.TEN);

        final Card result = executor.execute(transaction);

        assertThat(result).isEqualTo(card);
        verify(chain).handle(any(TransactionContext.class));
        verifyNoMoreInteractions(chain);
        verifyNoInteractions(eventPublisher);
    }
}
