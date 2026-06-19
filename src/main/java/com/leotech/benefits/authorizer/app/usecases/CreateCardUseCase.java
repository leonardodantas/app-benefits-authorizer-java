package com.leotech.benefits.authorizer.app.usecases;

import com.leotech.benefits.authorizer.domain.card.Card;

public interface CreateCardUseCase {

    Card execute(Card card);
}
