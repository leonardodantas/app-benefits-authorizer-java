package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;

public record TransactionContext(
        Transaction transaction,
        Card card,
        HandlerStatus status,
        CustomException exception
) {
    public TransactionContext(final Transaction transaction) {
        this(transaction, null, HandlerStatus.CONTINUE, null);
    }

    public TransactionContext withCard(final Card card) {
        return new TransactionContext(transaction, card, status, exception);
    }

    public TransactionContext withStatus(final HandlerStatus status) {
        return new TransactionContext(transaction, card, status, exception);
    }

    public TransactionContext withException(final CustomException exception) {
        return new TransactionContext(transaction, card, status, exception);
    }
}
