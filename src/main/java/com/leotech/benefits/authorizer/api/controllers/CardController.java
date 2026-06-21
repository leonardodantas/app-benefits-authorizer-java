package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.mappers.CardMapper;
import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.requests.UpdateCardStatusRequest;
import com.leotech.benefits.authorizer.api.responses.CardSummaryResponse;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.app.usecases.ListCardsUseCase;
import com.leotech.benefits.authorizer.app.usecases.UpdateCardStatusUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cartões", description = "Operações sobre cartões de benefícios")
public class CardController {

    private final CreateCardUseCase createCardUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final ListCardsUseCase listCardsUseCase;
    private final UpdateCardStatusUseCase updateCardStatusUseCase;
    private final CardMapper cardMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo cartão", description = "Cria um novo cartão com número e senha")
    @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso")
    @ApiResponse(responseCode = "422", description = "Cartão já existe", content = @Content)
    public CreateCardResponse create(@RequestBody @Valid final CreateCardRequest request) {
        log.info("Creating card {}", request.cardNumber());
        final Card card = cardMapper.toDomain(request);
        final Card createdCard = createCardUseCase.execute(card);
        log.info("Card {} created with balance {}", createdCard.cardNumber(), createdCard.balance());
        return cardMapper.toResponse(createdCard);
    }

    @GetMapping("/{numeroCartao}")
    @Operation(summary = "Obter saldo do cartão", description = "Retorna o saldo atual do cartão")
    @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso",
            content = @Content(schema = @Schema(implementation = BigDecimal.class),
                    examples = @ExampleObject("495.15")))
    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
    public BigDecimal getBalance(@PathVariable("numeroCartao") final String cardNumber) {
        log.info("Getting balance for card {}", cardNumber);
        final BigDecimal balance = getBalanceUseCase.execute(cardNumber);
        log.info("Balance for card {} is {}", cardNumber, balance);
        return balance;
    }

    @GetMapping
    @Operation(summary = "Listar cartões", description = "Retorna a lista paginada de cartões")
    public Page<CardSummaryResponse> list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        log.info("Listing cards page={}, size={}", page, size);
        return listCardsUseCase.execute(page, size)
                .map(cardMapper::toSummaryResponse);
    }

    @PatchMapping("/{numeroCartao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atualizar status do cartão", description = "Atualiza o status do cartão (ATIVO/BLOQUEADO). Idempotente.")
    public void updateStatus(
            @PathVariable("numeroCartao") final String cardNumber,
            @RequestBody @Valid final UpdateCardStatusRequest request) {
        log.info("Updating status of card {} to {}", cardNumber, request.status());
        updateCardStatusUseCase.execute(cardNumber, request.status());
    }
}
