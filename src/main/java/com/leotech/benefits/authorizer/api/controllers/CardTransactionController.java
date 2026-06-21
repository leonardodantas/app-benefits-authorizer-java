package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.mappers.TransactionMapper;
import com.leotech.benefits.authorizer.api.responses.TransactionLogResponse;
import com.leotech.benefits.authorizer.app.usecases.GetTransactionHistoryUseCase;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Historico", description = "Histórico de transações do cartão")
public class CardTransactionController {

    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
    private final TransactionMapper transactionMapper;

    @GetMapping("{numeroCartao}/transacoes")
    @Operation(summary = "Obter histórico de transações", description = "Retorna o histórico paginado de transações de um cartão, com filtro opcional por status")
    public Page<TransactionLogResponse> getHistory(
            @PathVariable("numeroCartao") final String cardNumber,
            @RequestParam(required = false) final TransactionStatus status,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        log.info("Getting transaction history for card {}, status={}, page={}, size={}", cardNumber, status, page, size);
        return getTransactionHistoryUseCase.execute(cardNumber, status, page, size)
                .map(transactionMapper::toResponse);
    }
}
