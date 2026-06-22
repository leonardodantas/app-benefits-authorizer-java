package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import com.leotech.benefits.authorizer.domain.transaction.TransactionSystemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    private final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);
    private final TransactionContext context = new TransactionContext(transaction);

    @Nested
    @DisplayName("when handler succeeds")
    class WhenHandlerSucceeds {

        @Test
        @DisplayName("should call doHandle then propagate to next")
        void shouldCallDoHandleAndPropagate() {
            final TransactionHandler first = mock(TransactionHandler.class, CALLS_REAL_METHODS);
            final TransactionHandler second = mock(TransactionHandler.class, CALLS_REAL_METHODS);
            final TransactionHandler third = mock(TransactionHandler.class, CALLS_REAL_METHODS);

            first.then(second);
            second.then(third);

            when(first.doHandle(any(TransactionContext.class))).thenAnswer(invocation -> {
                final TransactionContext ctx = invocation.getArgument(0);
                return ctx.withStatus(HandlerStatus.CONTINUE);
            });
            when(second.doHandle(any(TransactionContext.class))).thenAnswer(invocation -> {
                final TransactionContext ctx = invocation.getArgument(0);
                return ctx.withStatus(HandlerStatus.CONTINUE);
            });
            when(third.doHandle(any(TransactionContext.class))).thenAnswer(invocation -> {
                final TransactionContext ctx = invocation.getArgument(0);
                return ctx.withStatus(HandlerStatus.CONTINUE);
            });

            final TransactionContext result = first.handle(context);

            final InOrder order = inOrder(first, second, third);
            order.verify(first).doHandle(any(TransactionContext.class));
            order.verify(second).doHandle(any(TransactionContext.class));
            order.verify(third).doHandle(any(TransactionContext.class));
            assertThat(result.status()).isEqualTo(HandlerStatus.CONTINUE);
        }
    }

    @Nested
    @DisplayName("when handler stops")
    class WhenHandlerStops {

        @Test
        @DisplayName("should not propagate when status is not CONTINUE")
        void shouldNotPropagateWhenNotContinue() {
            final TransactionHandler first = mock(TransactionHandler.class, CALLS_REAL_METHODS);
            final TransactionHandler second = mock(TransactionHandler.class, CALLS_REAL_METHODS);

            first.then(second);

            when(first.doHandle(any(TransactionContext.class))).thenAnswer(invocation -> {
                final TransactionContext ctx = invocation.getArgument(0);
                return ctx.withStatus(HandlerStatus.STOP);
            });

            final TransactionContext result = first.handle(context);

            assertThat(result.status()).isEqualTo(HandlerStatus.STOP);
            verify(second, never()).doHandle(any(TransactionContext.class));
        }
    }

    @Nested
    @DisplayName("when handler throws")
    class WhenHandlerThrows {

        @Test
        @DisplayName("should catch exception and set STOP with TransactionSystemException")
        void shouldCatchException() {
            final TransactionHandler first = mock(TransactionHandler.class, CALLS_REAL_METHODS);
            final TransactionHandler second = mock(TransactionHandler.class, CALLS_REAL_METHODS);

            when(first.doHandle(any(TransactionContext.class))).thenThrow(new RuntimeException("unexpected"));

            first.then(second);

            final TransactionContext result = first.handle(context);

            assertThat(result.status()).isEqualTo(HandlerStatus.STOP);
            assertThat(result.exception()).isInstanceOf(TransactionSystemException.class);
            assertThat(result.exception()).hasMessage("SISTEMA_INTERMITENTE");
            verify(second, never()).doHandle(any(TransactionContext.class));
        }
    }
}
