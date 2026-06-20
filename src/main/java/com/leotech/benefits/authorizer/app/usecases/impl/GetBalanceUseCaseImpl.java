package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    private final CardRepository cardRepository;

    @Override
    public BigDecimal execute(final String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(Card::balance)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }
}
