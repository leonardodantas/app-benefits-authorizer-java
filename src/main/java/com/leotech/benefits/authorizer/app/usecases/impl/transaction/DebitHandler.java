package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DebitHandler extends TransactionHandler {

    private final CardRepository cardRepository;

    @Override
    protected void doHandle(final TransactionContext context) {
        final Card card = context.card();
        final BigDecimal amount = context.transaction().amount();

        final Card updated = card.toBuilder()
                .balance(card.balance().subtract(amount))
                .build();

        cardRepository.save(updated);
        context.setStatus(HandlerStatus.SUCCESS);
    }
}
