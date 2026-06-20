package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.CreateTransactionUseCase;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionExecutor transactionExecutor;

    @Override
    @Transactional
    public void execute(final Transaction transaction) {
        log.info("Processing transaction for card {}", transaction.cardNumber());
        transactionExecutor.execute(transaction);
        log.info("Transaction for card {} completed", transaction.cardNumber());
    }
}
