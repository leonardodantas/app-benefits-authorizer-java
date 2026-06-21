package com.leotech.benefits.authorizer.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String cardNumber,
        BigDecimal previousBalance,
        BigDecimal newBalance,
        BigDecimal amount,
        LocalDateTime timestamp
) {
}
