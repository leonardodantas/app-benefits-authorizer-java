package com.leotech.benefits.authorizer.config;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import com.leotech.benefits.authorizer.app.usecases.impl.transaction.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableJpaAuditing
public class TransactionChainConfig {

    @Bean
    public TransactionHandler transactionChain(final CardRepository cardRepository, final PasswordEncoder passwordEncoder) {
        final TransactionHandler debit = new DebitHandler(cardRepository);
        final TransactionHandler balanceValidation = new BalanceValidationHandler();
        final TransactionHandler passwordValidation = new PasswordValidationHandler(passwordEncoder);
        final TransactionHandler cardBlocked = new CardBlockedHandler();
        final TransactionHandler cardExistence = new CardExistenceHandler(cardRepository);

        cardExistence.setNext(cardBlocked);
        cardBlocked.setNext(passwordValidation);
        passwordValidation.setNext(balanceValidation);
        balanceValidation.setNext(debit);

        return cardExistence;
    }

}
