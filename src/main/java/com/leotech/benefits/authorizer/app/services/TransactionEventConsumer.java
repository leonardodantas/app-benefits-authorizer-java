package com.leotech.benefits.authorizer.app.services;

import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;

public interface TransactionEventConsumer {

    void consume(TransactionEvent event);
}
