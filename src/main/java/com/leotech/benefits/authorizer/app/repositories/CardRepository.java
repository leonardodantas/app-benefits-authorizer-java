package com.leotech.benefits.authorizer.app.repositories;

import com.leotech.benefits.authorizer.domain.card.Card;

import java.util.Optional;

public interface CardRepository {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findWithLockByCardNumber(String cardNumber);

    Card save(Card card);
}
