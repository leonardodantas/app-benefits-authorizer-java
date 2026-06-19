package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;

public class CardExistenceHandler extends TransactionHandler {

    private final CardRepository cardRepository;

    public CardExistenceHandler(final CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    protected void doHandle(final TransactionContext context) {
        final String cardNumber = context.transaction().cardNumber();
        final Card card = cardRepository.findWithLockByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotExistsException(cardNumber));
        context.setCard(card);
    }
}
