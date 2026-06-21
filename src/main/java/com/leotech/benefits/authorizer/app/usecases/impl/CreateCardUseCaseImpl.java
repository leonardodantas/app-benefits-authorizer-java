package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.config.AppProperties;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCardUseCaseImpl implements CreateCardUseCase {

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public Card execute(final Card card) {
        log.info("Creating card {}", card.cardNumber());
        validateCardExist(card);

        final Card cardToSave = getCardToSave(card);
        final Card saved = cardRepository.save(cardToSave);

        log.info("Card {} created with balance {}", saved.cardNumber(), saved.balance());
        return saved;
    }

    private void validateCardExist(final Card card) {
        cardRepository.findByCardNumber(card.cardNumber())
                .ifPresent(found -> {
                    throw new CardAlreadyExistsException(card.cardNumber());
                });
    }

    private Card getCardToSave(final Card card) {
        final String encryptedPassword = passwordEncoder.encode(card.password());
        final BigDecimal balance = appProperties.initialBalance();
        return card.toBuilder()
                .password(encryptedPassword)
                .balance(balance)
                .status(CardStatus.ACTIVE)
                .build();
    }
}
