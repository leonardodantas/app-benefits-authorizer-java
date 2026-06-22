package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.TransactionSystemException;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TransactionHandler {

    private TransactionHandler next;

    public TransactionHandler then(final TransactionHandler next) {
        this.next = next;
        return next;
    }

    public final TransactionContext handle(final TransactionContext context) {
        try {
            final TransactionContext newContext = doHandle(context);
            if (newContext.status() == HandlerStatus.CONTINUE && Objects.nonNull(next)) {
                return next.handle(newContext);
            }
            return newContext;
        } catch (final RuntimeException e) {
            log.error("Unexpected error in handler", e);
            return context.withStatus(HandlerStatus.STOP)
                    .withException(new TransactionSystemException());
        }
    }

    protected abstract TransactionContext doHandle(TransactionContext context);
}
