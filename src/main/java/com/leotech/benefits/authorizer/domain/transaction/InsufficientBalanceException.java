package com.leotech.benefits.authorizer.domain.transaction;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("SALDO_INSUFICIENTE");
    }
}
