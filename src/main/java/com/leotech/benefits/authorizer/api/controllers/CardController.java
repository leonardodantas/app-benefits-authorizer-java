package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.mappers.CardMapper;
import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
@Tag(name = "Cartões", description = "Operações sobre cartões de benefícios")
public class CardController {

    private final CreateCardUseCase createCardUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final CardMapper cardMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo cartão", description = "Cria um novo cartão com número e senha")
    @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso")
    @ApiResponse(responseCode = "422", description = "Cartão já existe", content = @Content)
    public CreateCardResponse create(@RequestBody @Valid final CreateCardRequest request) {
        final Card card = cardMapper.toDomain(request);
        final Card createdCard = createCardUseCase.execute(card);
        return cardMapper.toResponse(createdCard);
    }

    @GetMapping("/{numeroCartao}")
    @Operation(summary = "Obter saldo do cartão", description = "Retorna o saldo atual do cartão")
    @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso",
            content = @Content(schema = @Schema(implementation = BigDecimal.class),
                    examples = @ExampleObject("495.15")))
    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
    public BigDecimal getBalance(@PathVariable("numeroCartao") final String cardNumber) {
        return getBalanceUseCase.execute(cardNumber);
    }
}
