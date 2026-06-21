package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.AddBalanceUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddBalanceUseCaseImpl implements AddBalanceUseCase {

    private final CardRepository cardRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute(final String cardNumber, final BigDecimal amount) {
        log.info("Adding balance to card {}: {}", cardNumber, amount);

        final Card card = cardRepository.findWithLockByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));

        final BigDecimal previousBalance = card.balance();
        final BigDecimal newBalance = previousBalance.add(amount);

        final Card updated = card.toBuilder()
                .balance(newBalance)
                .build();

        cardRepository.save(updated);

        eventPublisher.publishEvent(TransactionEvent.success(cardNumber, previousBalance, newBalance, amount));
        log.info("Balance added to card {}: {} -> {}", cardNumber, previousBalance, newBalance);
    }
}
