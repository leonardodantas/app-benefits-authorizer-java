package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.UpdateCardStatusUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leotech.benefits.authorizer.domain.card.CardStatus.BLOCKED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCardStatusUseCaseImpl implements UpdateCardStatusUseCase {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public Card execute(final String cardNumber, final CardStatus newStatus) {
        log.info("Updating status of card {} to {}", cardNumber, newStatus);

        final Card card = cardRepository.findWithLockByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));

        if (card.status() == newStatus) {
            log.info("Card {} already has status {}, skipping update", cardNumber, newStatus);
            return card;
        }

        final Card updatedCard = getUpdateCard(newStatus, card);

        final Card saved = cardRepository.save(updatedCard);
        log.info("Card {} status updated to {}", cardNumber, saved.status());
        return saved;
    }

    private static Card getUpdateCard(final CardStatus newStatus, final Card card) {
        if (BLOCKED == newStatus) {
            return card.block();
        }
        return card.unblock();
    }
}
