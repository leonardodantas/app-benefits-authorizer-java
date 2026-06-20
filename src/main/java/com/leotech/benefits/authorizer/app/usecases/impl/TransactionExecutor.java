package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionExecutor {

    private final TransactionHandler chain;

    public Card execute(final Transaction transaction) {
        log.info("Starting transaction execution for card {}", transaction.cardNumber());
        final TransactionContext context = new TransactionContext(transaction);
        chain.handle(context);

        if (context.getStatus() == HandlerStatus.STOP && context.getException() != null) {
            log.warn("Transaction stopped for card {}: {}", transaction.cardNumber(), context.getException().getMessage());
            throw context.getException();
        }

        log.info("Transaction executed successfully for card {}", transaction.cardNumber());
        return context.getCard();
    }
}
