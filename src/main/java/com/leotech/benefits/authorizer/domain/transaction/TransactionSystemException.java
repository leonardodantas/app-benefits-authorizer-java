package com.leotech.benefits.authorizer.domain.transaction;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class TransactionSystemException extends CustomException {

    public TransactionSystemException() {
        super("SISTEMA_INTERMITENTE");
    }
}
