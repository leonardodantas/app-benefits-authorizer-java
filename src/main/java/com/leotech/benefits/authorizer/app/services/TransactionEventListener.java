package com.leotech.benefits.authorizer.app.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionEventConsumer eventConsumer;

    @Async
    @EventListener
    public void handleTransactionEvent(final TransactionEvent event) {
        eventConsumer.consume(event);
    }
}
