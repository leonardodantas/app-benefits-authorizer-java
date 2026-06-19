package com.leotech.benefits.authorizer.domain.transaction;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("SENHA_INVALIDA");
    }
}
