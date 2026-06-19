package com.leotech.benefits.authorizer.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank @Pattern(regexp = "^\\d{16}$") @JsonProperty("numeroCartao") @Schema(description = "Número do cartão", example = "6549873025634501") String cardNumber,
        @NotBlank @JsonProperty("senhaCartao") @Schema(description = "Senha do cartão", example = "1234") String password,
        @NotNull @DecimalMin("0.01") @JsonProperty("valor") @Schema(description = "Valor da transação", example = "10.00") BigDecimal amount
) {
}
