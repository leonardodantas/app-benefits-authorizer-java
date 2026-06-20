package com.leotech.benefits.authorizer.domain.transaction;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class InsufficientBalanceException extends CustomException {

    public InsufficientBalanceException() {
        super("SALDO_INSUFICIENTE");
    }
}
