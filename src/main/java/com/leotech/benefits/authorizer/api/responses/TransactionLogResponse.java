package com.leotech.benefits.authorizer.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionLogResponse(
        @JsonProperty("numeroCartao") @Schema(description = "Número do cartão", example = "6549873025634501") String cardNumber,
        @JsonProperty("status") @Schema(description = "Status da transação", example = "SUCCESS") TransactionStatus status,
        @JsonProperty("mensagem") @Schema(description = "Mensagem da transação", example = "TRANSACAO_APROVADA") String message,
        @JsonProperty("saldoAnterior") @Schema(description = "Saldo antes da transação", example = "500.00") BigDecimal previousBalance,
        @JsonProperty("novoSaldo") @Schema(description = "Saldo após a transação", example = "300.00") BigDecimal newBalance,
        @JsonProperty("valor") @Schema(description = "Valor da transação", example = "200.00") BigDecimal amount,
        @JsonProperty("dataHora") @Schema(description = "Data e hora da transação", example = "2026-06-20T10:00:00") LocalDateTime timestamp
) {
}
