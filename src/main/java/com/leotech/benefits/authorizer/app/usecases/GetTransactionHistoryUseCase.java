package com.leotech.benefits.authorizer.app.usecases;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import org.springframework.data.domain.Page;

public interface GetTransactionHistoryUseCase {

    Page<TransactionEvent> execute(String cardNumber, TransactionStatus status, int page, int size);
}
