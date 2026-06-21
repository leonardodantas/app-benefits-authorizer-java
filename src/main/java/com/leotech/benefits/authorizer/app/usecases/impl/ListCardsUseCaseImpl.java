package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.ListCardsUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListCardsUseCaseImpl implements ListCardsUseCase {

    private final CardRepository cardRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Card> execute(final int page, final int size) {
        log.info("Listing cards page={}, size={}", page, size);
        return cardRepository.findAll(PageRequest.of(page, size));
    }
}
