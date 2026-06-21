package com.leotech.benefits.authorizer.app.usecases;

import org.springframework.data.domain.Page;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;

public interface GetTransactionHistoryUseCase {

    Page<TransactionEvent> execute(String cardNumber, int page, int size);
}
