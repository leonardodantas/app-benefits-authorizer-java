package com.leotech.benefits.authorizer.domain.card;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(final String cardNumber) {
        super("Card " + cardNumber + " not found");
    }
}
