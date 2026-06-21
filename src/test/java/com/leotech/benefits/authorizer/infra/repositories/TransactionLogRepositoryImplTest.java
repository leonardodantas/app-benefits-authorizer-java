package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import com.leotech.benefits.authorizer.infra.mappers.TransactionLogInfraMapper;
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
class TransactionLogRepositoryImplTest {

    @Mock
    private JpaTransactionLogRepository jpaTransactionLogRepository;

    @Mock
    private TransactionLogInfraMapper transactionLogInfraMapper;

    @InjectMocks
    private TransactionLogRepositoryImpl repository;

    @Test
    @DisplayName("should find by card number and map to domain")
    void shouldFindByCardNumber() {
        final TransactionLogEntity entity = TransactionLogEntity.builder()
                .cardNumber("1234567890123456")
                .previousBalance(new BigDecimal("100.00"))
                .newBalance(new BigDecimal("70.00"))
                .amount(new BigDecimal("30.00"))
                .timestamp(LocalDateTime.now())
                .build();
        final TransactionEvent event = new TransactionEvent(
                "1234567890123456", new BigDecimal("100.00"), new BigDecimal("70.00"),
                new BigDecimal("30.00"), LocalDateTime.now());
        final Page<TransactionLogEntity> entityPage = new PageImpl<>(List.of(entity));
        final PageRequest pageRequest = PageRequest.of(0, 20);

        when(jpaTransactionLogRepository.findByCardNumberOrderByTimestampDesc("1234567890123456", pageRequest))
                .thenReturn(entityPage);
        when(transactionLogInfraMapper.toDomain(entity)).thenReturn(event);

        final Page<TransactionEvent> result = repository.findByCardNumber("1234567890123456", pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("1234567890123456");
        verify(jpaTransactionLogRepository).findByCardNumberOrderByTimestampDesc("1234567890123456", pageRequest);
        verify(transactionLogInfraMapper).toDomain(entity);
        verifyNoMoreInteractions(jpaTransactionLogRepository, transactionLogInfraMapper);
    }

    @Test
    @DisplayName("should return empty page when no transactions found")
    void shouldReturnEmptyPage() {
        final PageRequest pageRequest = PageRequest.of(0, 20);
        final Page<TransactionLogEntity> emptyPage = Page.empty();

        when(jpaTransactionLogRepository.findByCardNumberOrderByTimestampDesc("1234567890123456", pageRequest))
                .thenReturn(emptyPage);

        final Page<TransactionEvent> result = repository.findByCardNumber("1234567890123456", pageRequest);

        assertThat(result.getContent()).isEmpty();
        verify(jpaTransactionLogRepository).findByCardNumberOrderByTimestampDesc("1234567890123456", pageRequest);
        verifyNoInteractions(transactionLogInfraMapper);
    }
}
