package com.leotech.benefits.authorizer.app.usecases;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardStatus;

public interface UpdateCardStatusUseCase {

    Card execute(String cardNumber, CardStatus newStatus);
}
