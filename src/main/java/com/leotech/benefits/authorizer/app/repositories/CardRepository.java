package com.leotech.benefits.authorizer.app.repositories;

import com.leotech.benefits.authorizer.domain.card.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CardRepository {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findWithLockByCardNumber(String cardNumber);

    Page<Card> findAll(Pageable pageable);

    Card save(Card card);
}
