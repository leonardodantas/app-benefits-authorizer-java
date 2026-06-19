package com.leotech.benefits.authorizer.domain.transaction;

import java.math.BigDecimal;

public record Transaction(
        String cardNumber,
        String password,
        BigDecimal amount
) {
}
