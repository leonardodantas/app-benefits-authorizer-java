package com.leotech.benefits.authorizer.config;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionChainConfig {

    @Bean
    public TransactionHandler transactionChain(final CardRepository cardRepository, final PasswordEncoder passwordEncoder) {
        final TransactionHandler terminal = new TerminalHandler();
        final TransactionHandler balanceValidation = new BalanceValidationHandler();
        final TransactionHandler passwordValidation = new PasswordValidationHandler(passwordEncoder);
        final TransactionHandler cardExistence = new CardExistenceHandler(cardRepository);

        cardExistence.setNext(passwordValidation);
        passwordValidation.setNext(balanceValidation);
        balanceValidation.setNext(terminal);

        return cardExistence;
    }
}
