package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    private final CardRepository cardRepository;

    @Override
    public BigDecimal execute(final String cardNumber) {
        log.info("Getting balance for card {}", cardNumber);
        final BigDecimal balance = cardRepository.findByCardNumber(cardNumber)
                .map(Card::balance)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
        log.info("Balance for card {} is {}", cardNumber, balance);
        return balance;
    }
}
