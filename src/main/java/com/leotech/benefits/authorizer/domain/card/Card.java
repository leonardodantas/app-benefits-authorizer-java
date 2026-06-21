package com.leotech.benefits.authorizer.domain.card;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record Card(
        Long id,
        String cardNumber,
        String password,
        BigDecimal balance,
        CardStatus status
) {
    public Card block() {
        return toBuilder().status(CardStatus.BLOCKED).build();
    }

    public Card unblock() {
        return toBuilder().status(CardStatus.ACTIVE).build();
    }
}
