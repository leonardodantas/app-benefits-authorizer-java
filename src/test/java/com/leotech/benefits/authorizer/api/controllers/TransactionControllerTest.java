package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.handlers.ApiExceptionHandler;
import com.leotech.benefits.authorizer.api.mappers.TransactionMapper;
import com.leotech.benefits.authorizer.api.requests.CreateTransactionRequest;
import com.leotech.benefits.authorizer.app.usecases.CreateTransactionUseCase;
import com.leotech.benefits.authorizer.domain.transaction.CardNotExistsException;
import com.leotech.benefits.authorizer.domain.transaction.InsufficientBalanceException;
import com.leotech.benefits.authorizer.domain.transaction.InvalidPasswordException;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTransactionUseCase createTransactionUseCase;

    @MockitoBean
    private TransactionMapper transactionMapper;

    private static final String CARD_NUMBER = "6549873025634501";
    private static final String PASSWORD = "1234";
    private static final BigDecimal AMOUNT = new BigDecimal("10.00");

    @Nested
    @DisplayName("POST /transacoes")
    class CreateTransaction {

        @Test
        @DisplayName("should return 201 with OK when transaction succeeds")
        void shouldReturn201() throws Exception {
            final var request = new CreateTransactionRequest(CARD_NUMBER, PASSWORD, AMOUNT);
            final var domain = new Transaction(CARD_NUMBER, PASSWORD, AMOUNT);

            when(transactionMapper.toDomain(request)).thenReturn(domain);

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "%s", "senhaCartao": "%s", "valor": %s}
                                    """.formatted(CARD_NUMBER, PASSWORD, AMOUNT)))
                    .andExpect(status().isCreated());

            verify(transactionMapper).toDomain(request);
            verify(createTransactionUseCase).execute(domain);
            verifyNoMoreInteractions(createTransactionUseCase, transactionMapper);
        }

        @Test
        @DisplayName("should return 422 with CARTAO_INEXISTENTE when card does not exist")
        void shouldReturn422CardNotExists() throws Exception {
            final var request = new CreateTransactionRequest("9999999999999999", PASSWORD, AMOUNT);
            final var domain = new Transaction("9999999999999999", PASSWORD, AMOUNT);

            when(transactionMapper.toDomain(request)).thenReturn(domain);
            doThrow(new CardNotExistsException("9999999999999999"))
                    .when(createTransactionUseCase).execute(domain);

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "9999999999999999", "senhaCartao": "%s", "valor": %s}
                                    """.formatted(PASSWORD, AMOUNT)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("CARTAO_INEXISTENTE"));
        }

        @Test
        @DisplayName("should return 422 with SENHA_INVALIDA when password is wrong")
        void shouldReturn422InvalidPassword() throws Exception {
            final var request = new CreateTransactionRequest(CARD_NUMBER, "wrong", AMOUNT);
            final var domain = new Transaction(CARD_NUMBER, "wrong", AMOUNT);

            when(transactionMapper.toDomain(request)).thenReturn(domain);
            doThrow(new InvalidPasswordException())
                    .when(createTransactionUseCase).execute(domain);

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "%s", "senhaCartao": "wrong", "valor": %s}
                                    """.formatted(CARD_NUMBER, AMOUNT)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("SENHA_INVALIDA"));
        }

        @Test
        @DisplayName("should return 422 with SALDO_INSUFICIENTE when balance is insufficient")
        void shouldReturn422InsufficientBalance() throws Exception {
            final var request = new CreateTransactionRequest(CARD_NUMBER, PASSWORD, new BigDecimal("99999"));
            final var domain = new Transaction(CARD_NUMBER, PASSWORD, new BigDecimal("99999"));

            when(transactionMapper.toDomain(request)).thenReturn(domain);
            doThrow(new InsufficientBalanceException())
                    .when(createTransactionUseCase).execute(domain);

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "%s", "senhaCartao": "%s", "valor": 99999}
                                    """.formatted(CARD_NUMBER, PASSWORD)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("SALDO_INSUFICIENTE"));
        }

        @Test
        @DisplayName("should return 400 when request is invalid")
        void shouldReturn400() throws Exception {
            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors").isArray());

            verifyNoInteractions(createTransactionUseCase, transactionMapper);
        }
    }
}
