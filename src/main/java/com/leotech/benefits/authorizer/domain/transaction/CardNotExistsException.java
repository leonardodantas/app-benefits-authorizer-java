package com.leotech.benefits.authorizer.domain.transaction;

public class CardNotExistsException extends RuntimeException {

    public CardNotExistsException(final String cardNumber) {
        super("CARTAO_INEXISTENTE");
    }
}
