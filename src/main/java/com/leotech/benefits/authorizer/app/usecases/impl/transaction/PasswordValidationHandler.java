package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.domain.transaction.InvalidPasswordException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordValidationHandler extends TransactionHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    protected void doHandle(final TransactionContext context) {
        if (passwordEncoder.matches(context.transaction().password(), context.card().password())) {
            context.setStatus(HandlerStatus.CONTINUE);
            return;
        }

        context.setStatus(HandlerStatus.STOP);
        context.setException(new InvalidPasswordException());
    }
}
