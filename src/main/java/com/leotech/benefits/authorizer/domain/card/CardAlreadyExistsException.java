package com.leotech.benefits.authorizer.domain.card;

public class CardAlreadyExistsException extends RuntimeException {

    public CardAlreadyExistsException(final String cardNumber) {
        super("Card " + cardNumber + " already exists");
    }
}
