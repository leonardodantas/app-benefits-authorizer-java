package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.domain.transaction.InvalidPasswordException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PasswordValidationHandler extends TransactionHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    protected void doHandle(final TransactionContext context) {
        log.info("Validating password for card {}", context.getTransaction().cardNumber());
        if (passwordEncoder.matches(context.getTransaction().password(), context.getCard().password())) {
            log.info("Password validated for card {}", context.getTransaction().cardNumber());
            context.setStatus(HandlerStatus.CONTINUE);
            return;
        }

        log.warn("Invalid password for card {}", context.getTransaction().cardNumber());
        context.setStatus(HandlerStatus.STOP);
        context.setException(new InvalidPasswordException());
    }
}
