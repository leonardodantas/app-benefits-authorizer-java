package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TransactionContext {

    private final Transaction transaction;
    @Setter
    private Card card;
    @Setter
    private HandlerStatus status = HandlerStatus.CONTINUE;
    @Setter
    private CustomException exception;

    public TransactionContext(final Transaction transaction) {
        this.transaction = transaction;
    }
}
