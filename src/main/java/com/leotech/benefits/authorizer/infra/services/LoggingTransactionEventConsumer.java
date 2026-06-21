package com.leotech.benefits.authorizer.infra.services;

import com.leotech.benefits.authorizer.app.services.TransactionEventConsumer;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingTransactionEventConsumer implements TransactionEventConsumer {

    @Override
    public void consume(final TransactionEvent event) {
        log.info("Transaction event: card={}, previousBalance={}, newBalance={}, amount={}, timestamp={}",
                event.cardNumber(), event.previousBalance(), event.newBalance(),
                event.amount(), event.timestamp());
    }
}
