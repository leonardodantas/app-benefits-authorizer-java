package com.leotech.benefits.authorizer.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @Schema(description = "Código HTTP do erro") int status,
        @Schema(description = "Mensagem de erro") String message,
        @Schema(description = "Erros de validação, quando houver") List<FieldError> errors
) {

    public record FieldError(
            @Schema(description = "Campo com erro") String field,
            @Schema(description = "Mensagem do erro") String message
    ) {
    }
}
