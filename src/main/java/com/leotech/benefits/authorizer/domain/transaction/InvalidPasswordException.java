package com.leotech.benefits.authorizer.domain.transaction;

import com.leotech.benefits.authorizer.domain.shared.CustomException;

public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super("SENHA_INVALIDA");
    }
}
