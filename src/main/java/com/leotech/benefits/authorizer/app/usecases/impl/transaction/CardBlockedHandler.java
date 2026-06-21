package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.CardBlockedException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardBlockedHandler extends TransactionHandler {

    @Override
    protected void doHandle(final TransactionContext context) {
        final var card = context.getCard();
        if (card.status() == CardStatus.BLOCKED) {
            log.warn("Card {} is blocked", card.cardNumber());
            context.setStatus(HandlerStatus.STOP);
            context.setException(new CardBlockedException());
            return;
        }
        context.setStatus(HandlerStatus.CONTINUE);
    }
}
