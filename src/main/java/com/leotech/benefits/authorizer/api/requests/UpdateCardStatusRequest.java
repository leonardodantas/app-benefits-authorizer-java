package com.leotech.benefits.authorizer.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateCardStatusRequest(
        @NotNull @JsonProperty("status") @Schema(description = "Novo status do cartão", example = "BLOCKED") CardStatus status
) {
}
