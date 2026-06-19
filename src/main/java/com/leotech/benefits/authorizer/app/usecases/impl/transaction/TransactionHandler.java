package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import java.util.Objects;

public abstract class TransactionHandler {

    private TransactionHandler next;

    public void setNext(final TransactionHandler next) {
        this.next = next;
    }

    public final void handle(final TransactionContext context) {
        doHandle(context);
        if(Objects.nonNull(next)) {
            next.handle(context);
        }
    }

    protected abstract void doHandle(TransactionContext context);
}
