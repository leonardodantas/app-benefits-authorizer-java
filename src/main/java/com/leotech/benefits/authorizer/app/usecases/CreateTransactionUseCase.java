package com.leotech.benefits.authorizer.app.usecases;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;

public interface CreateTransactionUseCase {

    void execute(Transaction transaction);
}
