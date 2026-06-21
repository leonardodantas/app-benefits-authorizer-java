package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import com.leotech.benefits.authorizer.infra.mappers.CardInfraMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardRepositoryImpl implements CardRepository {

    private final JpaCardRepository jpaCardRepository;
    private final CardInfraMapper cardInfraMapper;

    @Override
    public Optional<Card> findByCardNumber(final String cardNumber) {
        log.debug("Finding card by number {}", cardNumber);
        return jpaCardRepository.findByCardNumber(cardNumber)
                .map(cardInfraMapper::toDomain);
    }

    @Override
    public Optional<Card> findWithLockByCardNumber(final String cardNumber) {
        log.debug("Finding card with lock by number {}", cardNumber);
        return jpaCardRepository.findWithLockByCardNumber(cardNumber)
                .map(cardInfraMapper::toDomain);
    }

    @Override
    public Page<Card> findAll(final Pageable pageable) {
        log.debug("Finding all cards with pagination");
        return jpaCardRepository.findAll(pageable)
                .map(cardInfraMapper::toDomain);
    }

    @Override
    public Card save(final Card card) {
        log.debug("Saving card {}", card.cardNumber());
        final CardEntity entity = cardInfraMapper.toEntity(card);
        final CardEntity saved = jpaCardRepository.save(entity);
        return cardInfraMapper.toDomain(saved);
    }
}
