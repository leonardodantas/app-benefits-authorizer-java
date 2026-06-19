package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;

class TerminalHandlerTest {

    @Test
    @DisplayName("should do nothing without throwing")
    void shouldDoNothing() {
        final Transaction transaction = new Transaction("123", "senha", BigDecimal.TEN);
        final TransactionContext context = new TransactionContext(transaction);

        final TerminalHandler handler = new TerminalHandler();

        assertThatCode(() -> handler.doHandle(context))
                .doesNotThrowAnyException();
    }
}
