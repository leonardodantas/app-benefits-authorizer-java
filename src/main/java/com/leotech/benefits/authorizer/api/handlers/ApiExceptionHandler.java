package com.leotech.benefits.authorizer.api.handlers;

import com.leotech.benefits.authorizer.api.responses.ErrorResponse;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import com.leotech.benefits.authorizer.domain.shared.CustomException;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;
import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;
import com.leotech.benefits.authorizer.domain.transaction.InvalidPasswordException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final MethodArgumentNotValidException ex) {
        final List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        final ErrorResponse response = new ErrorResponse(
                400,
                VALIDATION_FAILED,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyExists(final CardAlreadyExistsException ex) {
        final ErrorResponse response = new ErrorResponse(
                422,
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(422).body(response);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Void> handleCardNotFound(final CardNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CardNotExistsException.class)
    public ResponseEntity<String> handleCardNotExists(final CardNotExistsException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPassword(final InvalidPasswordException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalance(final InsufficientBalanceException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(final CustomException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(final RuntimeException ex) {
        final ErrorResponse response = new ErrorResponse(
                500,
                INTERNAL_SERVER_ERROR,
                null
        );

        return ResponseEntity.status(500).body(response);
    }
}
