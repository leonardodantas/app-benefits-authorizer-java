package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.TransactionLogRepository;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionHistoryUseCaseImplTest {

    @Mock
    private TransactionLogRepository transactionLogRepository;

    @InjectMocks
    private GetTransactionHistoryUseCaseImpl useCase;

    @Test
    @DisplayName("should return paginated history")
    void shouldReturnPaginatedHistory() {
        final TransactionEvent event = new TransactionEvent(
                "123", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, LocalDateTime.now());
        final Page<TransactionEvent> page = new PageImpl<>(List.of(event));

        when(transactionLogRepository.findByCardNumber("123", PageRequest.of(0, 20)))
                .thenReturn(page);

        final Page<TransactionEvent> result = useCase.execute("123", 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("123");
        verify(transactionLogRepository).findByCardNumber("123", PageRequest.of(0, 20));
        verifyNoMoreInteractions(transactionLogRepository);
    }

    @Test
    @DisplayName("should return empty page when no transactions")
    void shouldReturnEmptyPage() {
        final Page<TransactionEvent> emptyPage = Page.empty();

        when(transactionLogRepository.findByCardNumber("123", PageRequest.of(0, 20)))
                .thenReturn(emptyPage);

        final Page<TransactionEvent> result = useCase.execute("123", 0, 20);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(transactionLogRepository).findByCardNumber("123", PageRequest.of(0, 20));
        verifyNoMoreInteractions(transactionLogRepository);
    }
}
