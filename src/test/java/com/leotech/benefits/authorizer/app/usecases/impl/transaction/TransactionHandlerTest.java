package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    private final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);
    private final TransactionContext context = new TransactionContext(transaction);

    @Test
    @DisplayName("should call doHandle then propagate to next")
    void shouldCallDoHandleAndPropagate() {
        final TransactionHandler first = mock(TransactionHandler.class, CALLS_REAL_METHODS);
        final TransactionHandler second = mock(TransactionHandler.class, CALLS_REAL_METHODS);
        final TransactionHandler third = mock(TransactionHandler.class, CALLS_REAL_METHODS);

        first.setNext(second);
        second.setNext(third);

        first.handle(context);

        final InOrder order = inOrder(first, second, third);
        order.verify(first).doHandle(context);
        order.verify(second).doHandle(context);
        order.verify(third).doHandle(context);
    }
}
