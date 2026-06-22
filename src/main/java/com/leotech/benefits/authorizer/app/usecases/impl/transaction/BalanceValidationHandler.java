package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BalanceValidationHandler extends TransactionHandler {

    @Override
    protected TransactionContext doHandle(final TransactionContext context) {
        final BigDecimal balance = context.card().balance();
        final BigDecimal amount = context.transaction().amount();

        log.info("Validating balance: {} >= {}", balance, amount);
        if (balance.compareTo(amount) >= 0) {
            log.info("Balance sufficient for card {}", context.transaction().cardNumber());
            return context.withStatus(HandlerStatus.CONTINUE);
        }

        log.warn("Insufficient balance for card {}", context.transaction().cardNumber());
        return context.withStatus(HandlerStatus.STOP)
                .withException(new InsufficientBalanceException());
    }
}
