package com.leotech.benefits.authorizer.api.handlers;

import com.leotech.benefits.authorizer.api.responses.ErrorResponse;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("should return 422 for generic CustomException")
    void shouldHandleCustomException() {
        final CustomException exception = new CustomException("CUSTOM_ERROR") {
        };

        final ResponseEntity<String> response = handler.handleCustomException(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isEqualTo("CUSTOM_ERROR");
    }

    @Test
    @DisplayName("should return 500 for generic RuntimeException")
    void shouldHandleRuntimeException() {
        final RuntimeException exception = new RuntimeException("unexpected");

        final ResponseEntity<ErrorResponse> response = handler.handleRuntime(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Internal server error");
    }
}
