package com.leotech.benefits.authorizer.infra.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.infra.repositories.JpaTransactionLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingTransactionEventConsumerTest {

    @Mock
    private JpaTransactionLogRepository transactionLogRepository;

    @InjectMocks
    private LoggingTransactionEventConsumer consumer;

    @Captor
    private ArgumentCaptor<com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity> entityCaptor;

    @Test
    @DisplayName("should persist transaction event")
    void shouldPersistEvent() {
        final TransactionEvent event = new TransactionEvent(
                "1234567890123456",
                new BigDecimal("100.00"),
                new BigDecimal("70.00"),
                new BigDecimal("30.00"),
                LocalDateTime.of(2026, 6, 20, 10, 0)
        );

        consumer.consume(event);

        verify(transactionLogRepository).save(entityCaptor.capture());

        final var entity = entityCaptor.getValue();

        assertThat(entity.getCardNumber()).isEqualTo("1234567890123456");
        assertThat(entity.getPreviousBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(entity.getNewBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(entity.getAmount()).isEqualByComparingTo(new BigDecimal("30.00"));

        verifyNoMoreInteractions(transactionLogRepository);
    }
}
