package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTransactionLogRepository extends JpaRepository<TransactionLogEntity, Long> {

    Page<TransactionLogEntity> findByCardNumberOrderByTimestampDesc(String cardNumber, Pageable pageable);

    Page<TransactionLogEntity> findByCardNumberAndStatusOrderByTimestampDesc(
            String cardNumber, TransactionStatus status, Pageable pageable);
}
