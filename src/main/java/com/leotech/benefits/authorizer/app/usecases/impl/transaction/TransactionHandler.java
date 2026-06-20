package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.TransactionSystemException;

import java.util.Objects;

public abstract class TransactionHandler {

    private TransactionHandler next;

    public void setNext(final TransactionHandler next) {
        this.next = next;
    }

    public final void handle(final TransactionContext context) {
        try {
            doHandle(context);
            if (context.status() == HandlerStatus.CONTINUE && Objects.nonNull(next)) {
                next.handle(context);
            }
        } catch (final RuntimeException e) {
            context.setStatus(HandlerStatus.STOP);
            context.setException(new TransactionSystemException());
        }
    }

    protected abstract void doHandle(TransactionContext context);
}
