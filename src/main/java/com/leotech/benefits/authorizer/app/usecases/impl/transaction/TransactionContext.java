package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.Setter;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class TransactionContext {

    private final Transaction transaction;
    @Setter
    private Card card;
    private HandlerStatus status = HandlerStatus.CONTINUE;
    private CustomException exception;

    public TransactionContext(final Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction transaction() {
        return transaction;
    }

    public Card card() {
        return card;
    }

    public HandlerStatus status() {
        return status;
    }

    public void setStatus(final HandlerStatus status) {
        this.status = status;
    }

    public CustomException exception() {
        return exception;
    }

    public void setException(final CustomException exception) {
        this.exception = exception;
    }

}
