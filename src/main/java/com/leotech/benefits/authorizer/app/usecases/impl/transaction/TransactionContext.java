package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.Setter;

public class TransactionContext {

    private final Transaction transaction;
    @Setter
    private Card card;

    public TransactionContext(final Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction transaction() {
        return transaction;
    }

    public Card card() {
        return card;
    }

}
