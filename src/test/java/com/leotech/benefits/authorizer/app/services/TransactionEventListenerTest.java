package com.leotech.benefits.authorizer.app.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEventListenerTest {

    @Mock
    private TransactionEventConsumer eventConsumer;

    @InjectMocks
    private TransactionEventListener eventListener;

    @Test
    @DisplayName("should delegate success event to consumer")
    void shouldDelegateSuccessEvent() {
        final TransactionEvent event = TransactionEvent.success(
                "123", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN);

        eventListener.handleTransactionEvent(event);

        verify(eventConsumer).consume(event);
        verifyNoMoreInteractions(eventConsumer);
    }

    @Test
    @DisplayName("should delegate error event to consumer")
    void shouldDelegateErrorEvent() {
        final TransactionEvent event = TransactionEvent.error("123", "SALDO_INSUFICIENTE");

        eventListener.handleTransactionEvent(event);

        verify(eventConsumer).consume(event);
        verifyNoMoreInteractions(eventConsumer);
    }
}
