package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public class EventPublisherHandler extends TransactionHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doHandle(final TransactionContext context) {
        final BigDecimal previousBalance = context.getCard().balance();
        final BigDecimal amount = context.getTransaction().amount();
        final BigDecimal newBalance = previousBalance.subtract(amount);

        final TransactionEvent event = new TransactionEvent(
                context.getCard().cardNumber(),
                previousBalance,
                newBalance,
                amount,
                LocalDateTime.now()
        );

        eventPublisher.publishEvent(event);
        log.info("Transaction event published for card {}", event.cardNumber());
        context.setStatus(HandlerStatus.SUCCESS);
    }
}
