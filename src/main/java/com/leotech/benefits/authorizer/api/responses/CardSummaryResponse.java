package com.leotech.benefits.authorizer.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CardSummaryResponse(
        @JsonProperty("numeroCartao") @Schema(description = "Número do cartão", example = "6549873025634501") String cardNumber,
        @JsonProperty("saldo") @Schema(description = "Saldo atual do cartão", example = "500.00") BigDecimal balance
) {
}
