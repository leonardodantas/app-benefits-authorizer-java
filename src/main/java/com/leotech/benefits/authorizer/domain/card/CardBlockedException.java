package com.leotech.benefits.authorizer.domain.card;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class CardBlockedException extends CustomException {

    public CardBlockedException() {
        super("CARTAO_BLOQUEADO");
    }
}
