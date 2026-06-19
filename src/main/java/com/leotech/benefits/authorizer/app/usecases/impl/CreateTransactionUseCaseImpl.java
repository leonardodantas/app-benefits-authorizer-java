package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.usecases.CreateTransactionUseCase;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionContext;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.TransactionHandler;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionHandler transactionChain;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void execute(final Transaction transaction) {
        final TransactionContext ctx = new TransactionContext(transaction);

        transactionChain.handle(ctx);

        final Card updated = ctx.card().toBuilder()
                .balance(ctx.card().balance().subtract(transaction.amount()))
                .build();

        cardRepository.save(updated);
    }
}
