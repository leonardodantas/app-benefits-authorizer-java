package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.app.repositories.TransactionLogRepository;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import com.leotech.benefits.authorizer.infra.mappers.TransactionLogInfraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    private final JpaTransactionLogRepository jpaTransactionLogRepository;
    private final TransactionLogInfraMapper transactionLogInfraMapper;

    @Override
    public Page<TransactionEvent> findByCardNumber(final String cardNumber, final Pageable pageable) {
        return jpaTransactionLogRepository.findByCardNumberOrderByTimestampDesc(cardNumber, pageable)
                .map(transactionLogInfraMapper::toDomain);
    }

    @Override
    public Page<TransactionEvent> findByCardNumber(final String cardNumber, final TransactionStatus status,
                                                    final Pageable pageable) {
        return jpaTransactionLogRepository
                .findByCardNumberAndStatusOrderByTimestampDesc(cardNumber, status, pageable)
                .map(transactionLogInfraMapper::toDomain);
    }
}
