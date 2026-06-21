package com.leotech.benefits.authorizer.app.repositories;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionLogRepository {

    Page<TransactionEvent> findByCardNumber(String cardNumber, Pageable pageable);

    Page<TransactionEvent> findByCardNumber(String cardNumber, TransactionStatus status, Pageable pageable);
}
