package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

public final class TerminalHandler extends TransactionHandler {

    @Override
    protected void doHandle(final TransactionContext context) {
        context.setStatus(HandlerStatus.SUCCESS);
    }
}
