package com.leotech.benefits.authorizer.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddBalanceRequest(
        @NotNull @DecimalMin("0.01") @JsonProperty("valor") @Schema(description = "Valor a ser adicionado", example = "50.00") BigDecimal amount
) {
}
