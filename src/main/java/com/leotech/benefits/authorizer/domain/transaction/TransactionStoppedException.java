package com.leotech.benefits.authorizer.domain.transaction;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class TransactionStoppedException extends CustomException {

    public TransactionStoppedException() {
        super("TRANSACAO_INTERROMPIDA");
    }
}
