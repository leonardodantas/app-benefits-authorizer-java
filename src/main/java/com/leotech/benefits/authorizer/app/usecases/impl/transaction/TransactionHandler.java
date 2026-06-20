package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.TransactionSystemException;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TransactionHandler {

    private TransactionHandler next;

    public void setNext(final TransactionHandler next) {
        this.next = next;
    }

    public final void handle(final TransactionContext context) {
        try {
            doHandle(context);
            if (context.getStatus() == HandlerStatus.CONTINUE && Objects.nonNull(next)) {
                next.handle(context);
            }
        } catch (final RuntimeException e) {
            log.error("Unexpected error in handler", e);
            context.setStatus(HandlerStatus.STOP);
            context.setException(new TransactionSystemException());
        }
    }

    protected abstract void doHandle(TransactionContext context);
}
