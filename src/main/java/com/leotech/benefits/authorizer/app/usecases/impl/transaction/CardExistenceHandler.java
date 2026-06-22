package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CardExistenceHandler extends TransactionHandler {

    private final CardRepository cardRepository;

    @Override
    protected TransactionContext doHandle(final TransactionContext context) {
        final String cardNumber = context.transaction().cardNumber();
        log.info("Checking existence of card {}", cardNumber);
        final Optional<Card> optionalCard = cardRepository.findWithLockByCardNumber(cardNumber);

        if (optionalCard.isPresent()) {
            log.info("Card {} found", cardNumber);
            return context.withCard(optionalCard.get())
                    .withStatus(HandlerStatus.CONTINUE);
        }

        log.warn("Card {} not found", cardNumber);
        return context.withStatus(HandlerStatus.STOP)
                .withException(new CardNotExistsException());
    }
}
