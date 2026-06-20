package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.CreateTransactionUseCase;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.HandlerStatus;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionExecutor transactionExecutor;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void execute(final Transaction transaction) {
        final TransactionExecutor.TransactionResult result = transactionExecutor.execute(transaction);

        if (result.status() != HandlerStatus.SUCCESS) {
            return;
        }

        final Card cardUpdated = result.card().toBuilder()
                .balance(result.card().balance().subtract(transaction.amount()))
                .build();

        cardRepository.save(cardUpdated);
    }
}
