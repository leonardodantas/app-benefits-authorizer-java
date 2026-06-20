package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;

import java.math.BigDecimal;

public class BalanceValidationHandler extends TransactionHandler {

    @Override
    protected void doHandle(final TransactionContext context) {
        final BigDecimal balance = context.card().balance();
        final BigDecimal amount = context.transaction().amount();

        if (balance.compareTo(amount) >= 0) {
            context.setStatus(HandlerStatus.CONTINUE);
            return;
        }

        context.setStatus(HandlerStatus.STOP);
        context.setException(new InsufficientBalanceException());
    }
}
