package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStoppedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionExecutor {

    private final TransactionHandler chain;
    private final ApplicationEventPublisher eventPublisher;

    public Card execute(final Transaction transaction) {
        log.info("Starting transaction execution for card {}", transaction.cardNumber());
        final TransactionContext context = chain.handle(new TransactionContext(transaction));

        if (context.status() == HandlerStatus.STOP) {
            if (Objects.nonNull(context.exception())) {
                log.warn("Transaction stopped for card {}: {}", transaction.cardNumber(), context.exception().getMessage());
                eventPublisher.publishEvent(TransactionEvent.error(
                        transaction.cardNumber(),
                        context.exception().getMessage()
                ));
                throw context.exception();
            }
            log.warn("Transaction stopped without exception for card {}", transaction.cardNumber());
            final TransactionStoppedException stopException = new TransactionStoppedException();
            eventPublisher.publishEvent(TransactionEvent.error(
                    transaction.cardNumber(),
                    stopException.getMessage()
            ));
            throw stopException;
        }

        log.info("Transaction executed successfully for card {}", transaction.cardNumber());
        eventPublisher.publishEvent(TransactionEvent.success(
                context.card().cardNumber(),
                context.card().balance().add(transaction.amount()),
                context.card().balance(),
                transaction.amount()
        ));
        return context.card();
    }
}
