package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.Setter;

public class TransactionContext {

    private final Transaction transaction;
    @Setter
    private Card card;
    @Setter
    private HandlerStatus status = HandlerStatus.CONTINUE;
    @Setter
    private CustomException exception;

    public TransactionContext(final Transaction transaction) {
        this.transaction = transaction;
    }

    public HandlerStatus status() {
        return status;
    }

    public CustomException exception() {
        return exception;
    }

    public Transaction transaction() {
        return transaction;
    }
}
