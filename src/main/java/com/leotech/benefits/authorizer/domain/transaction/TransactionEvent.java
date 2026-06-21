package com.leotech.benefits.authorizer.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String cardNumber,
        BigDecimal previousBalance,
        BigDecimal newBalance,
        BigDecimal amount,
        LocalDateTime timestamp,
        TransactionStatus status,
        String message
) {

    public static TransactionEvent success(final String cardNumber, final BigDecimal previousBalance,
                                            final BigDecimal newBalance, final BigDecimal amount) {
        return new TransactionEvent(cardNumber, previousBalance, newBalance, amount, LocalDateTime.now(),
                TransactionStatus.SUCCESS, "TRANSACAO_APROVADA");
    }

    public static TransactionEvent error(final String cardNumber, final String message) {
        return new TransactionEvent(cardNumber, null, null, null, LocalDateTime.now(),
                TransactionStatus.ERROR, message);
    }
}
