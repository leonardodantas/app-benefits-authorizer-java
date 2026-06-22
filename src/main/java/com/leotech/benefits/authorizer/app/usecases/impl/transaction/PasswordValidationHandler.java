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
    protected TransactionContext doHandle(final TransactionContext context) {
        log.info("Validating password for card {}", context.transaction().cardNumber());
        if (passwordEncoder.matches(context.transaction().password(), context.card().password())) {
            log.info("Password validated for card {}", context.transaction().cardNumber());
            return context.withStatus(HandlerStatus.CONTINUE);
        }

        log.warn("Invalid password for card {}", context.transaction().cardNumber());
        return context.withStatus(HandlerStatus.STOP)
                .withException(new InvalidPasswordException());
    }
}
