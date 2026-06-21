package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.infra.entities.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTransactionLogRepository extends JpaRepository<TransactionLogEntity, Long> {
}
