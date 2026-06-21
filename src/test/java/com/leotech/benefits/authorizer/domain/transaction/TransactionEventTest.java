package com.leotech.benefits.authorizer.domain.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionEventTest {

    @Test
    @DisplayName("should create success event with correct fields")
    void shouldCreateSuccessEvent() {
        final TransactionEvent event = TransactionEvent.success(
                "1234567890123456",
                new BigDecimal("100.00"),
                new BigDecimal("70.00"),
                new BigDecimal("30.00")
        );

        assertThat(event.cardNumber()).isEqualTo("1234567890123456");
        assertThat(event.previousBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(event.newBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(event.status()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(event.message()).isEqualTo("TRANSACAO_APROVADA");
        assertThat(event.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("should create error event with null values and message")
    void shouldCreateErrorEvent() {
        final TransactionEvent event = TransactionEvent.error("1234567890123456", "SALDO_INSUFICIENTE");

        assertThat(event.cardNumber()).isEqualTo("1234567890123456");
        assertThat(event.previousBalance()).isNull();
        assertThat(event.newBalance()).isNull();
        assertThat(event.amount()).isNull();
        assertThat(event.status()).isEqualTo(TransactionStatus.ERROR);
        assertThat(event.message()).isEqualTo("SALDO_INSUFICIENTE");
        assertThat(event.timestamp()).isNotNull();
    }
}
