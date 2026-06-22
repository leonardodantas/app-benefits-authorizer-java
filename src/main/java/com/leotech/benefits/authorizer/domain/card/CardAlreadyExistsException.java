package com.leotech.benefits.authorizer.domain.card;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class CardAlreadyExistsException extends CustomException {

    public CardAlreadyExistsException(final String cardNumber) {
        super("Cartão " + cardNumber + " já existe");
    }
}
