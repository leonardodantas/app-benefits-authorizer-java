package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;

import java.math.BigDecimal;

public class BalanceValidationHandler extends TransactionHandler {

    @Override
    protected void doHandle(final TransactionContext context) {
        final Card card = context.card();
        final BigDecimal amount = context.transaction().amount();

        if (card.balance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }
}
