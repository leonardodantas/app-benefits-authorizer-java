package com.leotech.benefits.authorizer.app.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionEventListener {

    @Async
    @EventListener
    public void handleTransactionEvent(final TransactionEvent event) {
        log.info("Processing async event: card={}, previousBalance={}, newBalance={}, amount={}, timestamp={}",
                event.cardNumber(), event.previousBalance(), event.newBalance(),
                event.amount(), event.timestamp());
    }
}
