package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TerminalHandlerTest {

    @Test
    @DisplayName("should set SUCCESS status")
    void shouldSetSuccessStatus() {
        final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);
        final TransactionContext context = new TransactionContext(transaction);

        final TerminalHandler handler = new TerminalHandler();

        handler.doHandle(context);

        assertThat(context.status()).isEqualTo(HandlerStatus.SUCCESS);
    }
}
