package com.leotech.benefits.authorizer.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCardResponse(
        @JsonProperty("numeroCartao") @Schema(description = "Número do cartão criado", example = "123456789") String cardNumber
) {
}
