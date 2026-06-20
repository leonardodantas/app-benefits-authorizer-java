package com.leotech.benefits.authorizer.domain.card;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record Card(
        Long id,
        String cardNumber,
        String password,
        BigDecimal balance
) {
}
