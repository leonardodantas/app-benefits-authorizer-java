package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardBlockedException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardBlockedHandler extends TransactionHandler {

    @Override
    protected TransactionContext doHandle(final TransactionContext context) {
        final Card card = context.card();
        if (card.status() == CardStatus.BLOCKED) {
            log.warn("Card {} is blocked", card.cardNumber());
            return context.withStatus(HandlerStatus.STOP)
                    .withException(new CardBlockedException());
        }
        return context.withStatus(HandlerStatus.CONTINUE);
    }
}
