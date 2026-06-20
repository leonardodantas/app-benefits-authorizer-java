package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardExistenceHandler extends TransactionHandler {

    private final CardRepository cardRepository;

    @Override
    protected void doHandle(final TransactionContext context) {
        final String cardNumber = context.transaction().cardNumber();
        final Optional<Card> optionalCard = cardRepository.findWithLockByCardNumber(cardNumber);

        if (optionalCard.isPresent()) {
            context.setCard(optionalCard.get());
            context.setStatus(HandlerStatus.CONTINUE);
            return;
        }

        context.setStatus(HandlerStatus.STOP);
        context.setException(new CardNotExistsException());
    }
}
