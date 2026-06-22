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
        final CardExistenceHandler first = new CardExistenceHandler(cardRepository);
        first.then(new CardBlockedHandler())
                .then(new PasswordValidationHandler(passwordEncoder))
                .then(new BalanceValidationHandler())
                .then(new DebitHandler(cardRepository));
        return first;
    }

}
