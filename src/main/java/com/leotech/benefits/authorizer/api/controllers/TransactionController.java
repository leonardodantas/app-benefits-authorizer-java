package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.mappers.TransactionMapper;
import com.leotech.benefits.authorizer.api.requests.CreateTransactionRequest;
import com.leotech.benefits.authorizer.app.usecases.CreateTransactionUseCase;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transações", description = "Operações sobre transações")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final TransactionMapper transactionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realizar transação", description = "Processa uma transação com cartão, senha e valor")
    @ApiResponse(responseCode = "201", description = "Transação realizada com sucesso",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "422", description = "Regra de autorização barrou a transação",
            content = @Content(mediaType = "text/plain"))
    public void create(@RequestBody @Valid final CreateTransactionRequest request) {
        log.info("Processing transaction for card {}", request.cardNumber());
        final Transaction transaction = transactionMapper.toDomain(request);

        createTransactionUseCase.execute(transaction);
        log.info("Transaction for card {} completed", request.cardNumber());
    }
}
