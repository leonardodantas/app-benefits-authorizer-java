package com.leotech.benefits.authorizer.domain.card;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class CardNotFoundException extends CustomException {

    public CardNotFoundException(final String cardNumber) {
        super("Card " + cardNumber + " not found");
    }
}
