package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

public final class TerminalHandler extends TransactionHandler {

    @Override
    protected void doHandle(final TransactionContext context) {
        System.out.println("AQUI");
    }
}
