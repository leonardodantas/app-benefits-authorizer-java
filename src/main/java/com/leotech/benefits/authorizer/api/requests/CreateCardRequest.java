package com.leotech.benefits.authorizer.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCardRequest(
        @NotBlank(message = "O número do cartão é obrigatório") @Pattern(regexp = "^\\d{16}$", message = "O número do cartão deve ter 16 caracteres") @JsonProperty("numeroCartao") @Schema(description = "Número do cartão", example = "6549873025634501") String cardNumber,
        @NotBlank(message = "A senha é obrigatória") @JsonProperty("senha") @Schema(description = "Senha do cartão", example = "1234") String password
) {
}
