package com.leotech.benefits.authorizer.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank(message = "O número do cartão é obrigatório") @Pattern(regexp = "^\\d{16}$", message = "O número do cartão deve ter 16 caracteres") @JsonProperty("numeroCartao") @Schema(description = "Número do cartão", example = "6549873025634501") String cardNumber,
        @NotBlank(message = "A senha do cartão é obrigatória") @JsonProperty("senhaCartao") @Schema(description = "Senha do cartão", example = "1234") String password,
        @NotNull(message = "O valor é obrigatório") @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero") @JsonProperty("valor") @Schema(description = "Valor da transação", example = "10.00") BigDecimal amount
) {
}
