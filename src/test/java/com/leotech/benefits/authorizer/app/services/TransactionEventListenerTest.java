package com.leotech.benefits.authorizer.app.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
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
    @DisplayName("should delegate to consumer")
    void shouldDelegateToConsumer() {
        final TransactionEvent event = new TransactionEvent("123", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, LocalDateTime.now());

        eventListener.handleTransactionEvent(event);

        verify(eventConsumer).consume(event);
        verifyNoMoreInteractions(eventConsumer);
    }
}
