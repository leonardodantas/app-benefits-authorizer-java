package com.leotech.benefits.authorizer.app.usecases;

import com.leotech.benefits.authorizer.domain.card.Card;
import org.springframework.data.domain.Page;

public interface ListCardsUseCase {

    Page<Card> execute(int page, int size);
}
