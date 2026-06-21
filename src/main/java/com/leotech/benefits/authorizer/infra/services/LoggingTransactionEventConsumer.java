package com.leotech.benefits.authorizer.infra.services;

import com.leotech.benefits.authorizer.app.services.TransactionEventConsumer;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import com.leotech.benefits.authorizer.infra.mappers.TransactionLogInfraMapper;
import com.leotech.benefits.authorizer.infra.repositories.JpaTransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingTransactionEventConsumer implements TransactionEventConsumer {

    private final JpaTransactionLogRepository transactionLogRepository;
    private final TransactionLogInfraMapper transactionLogInfraMapper;

    @Override
    public void consume(final TransactionEvent event) {
        final TransactionLogEntity entity = transactionLogInfraMapper.toEntity(event);

        transactionLogRepository.save(entity);
        log.info("Transaction event persisted for card {}", event.cardNumber());
    }
}
