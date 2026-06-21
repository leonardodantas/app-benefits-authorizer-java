package com.leotech.benefits.authorizer.infra.services;

import com.leotech.benefits.authorizer.app.services.TransactionEventConsumer;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import com.leotech.benefits.authorizer.infra.repositories.JpaTransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingTransactionEventConsumer implements TransactionEventConsumer {

    private final JpaTransactionLogRepository transactionLogRepository;

    @Override
    public void consume(final TransactionEvent event) {
        final TransactionLogEntity entity = TransactionLogEntity.builder()
                .cardNumber(event.cardNumber())
                .previousBalance(event.previousBalance())
                .newBalance(event.newBalance())
                .amount(event.amount())
                .timestamp(event.timestamp())
                .build();

        transactionLogRepository.save(entity);
        log.info("Transaction event persisted for card {}", event.cardNumber());
    }
}
