package com.leotech.benefits.authorizer.domain.transaction;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class CardNotExistsException extends CustomException {

    public CardNotExistsException() {
        super("CARTAO_INEXISTENTE");
    }
}
