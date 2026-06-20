package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;

public class TransactionExecutor {

    private final TransactionHandler chain;

    public TransactionExecutor(final TransactionHandler chain) {
        this.chain = chain;
    }

    public TransactionResult execute(final Transaction transaction) {
        final TransactionContext context = new TransactionContext(transaction);
        chain.handle(context);

        if (context.status() == HandlerStatus.STOP && context.exception() != null) {
            throw context.exception();
        }

        return new TransactionResult(context.status(), context.card());
    }

    public record TransactionResult(HandlerStatus status, Card card) {
    }
}
