package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.config.AppProperties;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateCardUseCaseImpl implements CreateCardUseCase {

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public Card execute(final Card card) {
        cardRepository.findByCardNumber(card.cardNumber())
                .ifPresent(found -> {
                    throw new CardAlreadyExistsException(card.cardNumber());
                });

        final String encryptedPassword = passwordEncoder.encode(card.password());
        final BigDecimal balance = appProperties.initialBalance();
        final Card cardWithBalance = card.toBuilder()
                .password(encryptedPassword)
                .balance(balance)
                .build();

        return cardRepository.save(cardWithBalance);
    }
}
