package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public final class DebitHandler extends TransactionHandler {

    private final CardRepository cardRepository;

    @Override
    protected void doHandle(final TransactionContext context) {
        final Card card = context.card();
        final BigDecimal amount = context.transaction().amount();
        final BigDecimal newBalance = card.balance().subtract(amount);

        log.info("Debiting {} from card {}: {} -> {}", amount, card.cardNumber(), card.balance(), newBalance);

        final Card updated = card.toBuilder()
                .balance(newBalance)
                .build();

        cardRepository.save(updated);
        context.setStatus(HandlerStatus.SUCCESS);
        log.info("Card {} updated with new balance {}", card.cardNumber(), newBalance);
    }
}
