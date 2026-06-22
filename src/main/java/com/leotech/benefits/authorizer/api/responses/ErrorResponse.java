package com.leotech.benefits.authorizer.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @Schema(description = "Código HTTP do erro") int status,
        @Schema(description = "Mensagem de erro") String message,
        @Schema(description = "Timestamp do erro") LocalDateTime timestamp,
        @Schema(description = "Identificador único do erro") UUID errorId,
        @Schema(description = "Erros de validação, quando houver") List<FieldError> errors
) {

    public record FieldError(
            @Schema(description = "Campo com erro") String field,
            @Schema(description = "Mensagem do erro") String message
    ) {
    }
}
