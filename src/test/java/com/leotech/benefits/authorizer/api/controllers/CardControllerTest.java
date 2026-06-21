package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.handlers.ApiExceptionHandler;
import com.leotech.benefits.authorizer.api.mappers.CardMapper;
import com.leotech.benefits.authorizer.api.mappers.TransactionMapper;
import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.responses.CardSummaryResponse;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import com.leotech.benefits.authorizer.api.responses.TransactionLogResponse;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.app.usecases.GetTransactionHistoryUseCase;
import com.leotech.benefits.authorizer.app.usecases.ListCardsUseCase;
import com.leotech.benefits.authorizer.app.usecases.UpdateCardStatusUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCardUseCase createCardUseCase;

    @MockitoBean
    private GetBalanceUseCase getBalanceUseCase;

    @MockitoBean
    private ListCardsUseCase listCardsUseCase;

    @MockitoBean
    private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    @MockitoBean
    private UpdateCardStatusUseCase updateCardStatusUseCase;

    @MockitoBean
    private CardMapper cardMapper;

    @MockitoBean
    private TransactionMapper transactionMapper;

    private static final String CARD_NUMBER = "1234567890123456";
    private static final String PASSWORD = "1234";
    private static final BigDecimal BALANCE = new BigDecimal("495.15");

    @Nested
    @DisplayName("POST /cartoes")
    class CreateCard {

        @Test
        @DisplayName("should return 201 when card is created")
        void shouldReturn201() throws Exception {
            final CreateCardRequest request = new CreateCardRequest(CARD_NUMBER, PASSWORD);
            final Card domain = Card.builder().cardNumber(CARD_NUMBER).password(PASSWORD).build();
            final Card created = Card.builder().cardNumber(CARD_NUMBER).password(PASSWORD).balance(BALANCE).build();
            final CreateCardResponse response = new CreateCardResponse(CARD_NUMBER);

            when(cardMapper.toDomain(request)).thenReturn(domain);
            when(createCardUseCase.execute(domain)).thenReturn(created);
            when(cardMapper.toResponse(created)).thenReturn(response);

            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "%s", "senha": "%s"}
                                    """.formatted(CARD_NUMBER, PASSWORD)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.numeroCartao").value(CARD_NUMBER));

            verify(cardMapper).toDomain(request);
            verify(createCardUseCase).execute(domain);
            verify(cardMapper).toResponse(created);
            verifyNoMoreInteractions(createCardUseCase, getBalanceUseCase, cardMapper);
        }

        @Test
        @DisplayName("should return 400 when card number has invalid format")
        void shouldReturn400InvalidCardNumber() throws Exception {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "123", "senha": "1234"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors[0].field").value("cardNumber"))
                    .andExpect(jsonPath("$.errors[0].message").value("must match \"^\\d{16}$\""));

            verifyNoInteractions(createCardUseCase, getBalanceUseCase, cardMapper);
        }

        @Test
        @DisplayName("should return 400 when request is invalid")
        void shouldReturn400() throws Exception {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors").isArray());

            verifyNoInteractions(createCardUseCase, getBalanceUseCase, cardMapper);
        }

        @Test
        @DisplayName("should return 422 when card already exists")
        void shouldReturn422() throws Exception {
            final CreateCardRequest request = new CreateCardRequest(CARD_NUMBER, PASSWORD);
            final Card domain = Card.builder().cardNumber(CARD_NUMBER).password(PASSWORD).build();

            when(cardMapper.toDomain(request)).thenReturn(domain);
            when(createCardUseCase.execute(domain))
                    .thenThrow(new CardAlreadyExistsException(CARD_NUMBER));

            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"numeroCartao": "%s", "senha": "%s"}
                                    """.formatted(CARD_NUMBER, PASSWORD)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.message").value("Card " + CARD_NUMBER + " already exists"));

            verify(cardMapper).toDomain(request);
            verify(createCardUseCase).execute(domain);
            verifyNoMoreInteractions(createCardUseCase);
            verifyNoInteractions(getBalanceUseCase);
        }
    }

    @Nested
    @DisplayName("GET /cartoes/{numeroCartao}")
    class GetBalance {

        @Test
        @DisplayName("should return 200 and balance when card exists")
        void shouldReturn200() throws Exception {
            when(getBalanceUseCase.execute(CARD_NUMBER)).thenReturn(BALANCE);

            mockMvc.perform(get("/cartoes/{numeroCartao}", CARD_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(content().string("495.15"));

            verify(getBalanceUseCase).execute(CARD_NUMBER);
            verifyNoMoreInteractions(getBalanceUseCase);
            verifyNoInteractions(createCardUseCase, cardMapper);
        }

        @Test
        @DisplayName("should return 404 when card does not exist")
        void shouldReturn404() throws Exception {
            when(getBalanceUseCase.execute(CARD_NUMBER))
                    .thenThrow(new CardNotFoundException(CARD_NUMBER));

            mockMvc.perform(get("/cartoes/{numeroCartao}", CARD_NUMBER))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));

            verify(getBalanceUseCase).execute(CARD_NUMBER);
            verifyNoMoreInteractions(getBalanceUseCase);
            verifyNoInteractions(createCardUseCase, cardMapper);
        }
    }

    @Nested
    @DisplayName("GET /cartoes")
    class ListCards {

        @Test
        @DisplayName("should return 200 and paginated cards")
        void shouldReturn200() throws Exception {
            final Card card = Card.builder()
                    .cardNumber(CARD_NUMBER)
                    .balance(BALANCE)
                    .build();
            final Page<Card> page = new PageImpl<>(List.of(card));
            final CardSummaryResponse response = new CardSummaryResponse(CARD_NUMBER, BALANCE);

            when(listCardsUseCase.execute(0, 20)).thenReturn(page);
            when(cardMapper.toSummaryResponse(card)).thenReturn(response);

            mockMvc.perform(get("/cartoes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numeroCartao").value(CARD_NUMBER))
                    .andExpect(jsonPath("$.content[0].saldo").value(BALANCE));

            verify(listCardsUseCase).execute(0, 20);
            verify(cardMapper).toSummaryResponse(card);
        }
    }

    @Nested
    @DisplayName("PATCH /cartoes/{numeroCartao}")
    class UpdateCardStatus {

        @Test
        @DisplayName("should return 204 when blocking card")
        void shouldReturn204Block() throws Exception {
            mockMvc.perform(patch("/cartoes/{numeroCartao}", CARD_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "BLOCKED"}
                                    """))
                    .andExpect(status().isNoContent());

            verify(updateCardStatusUseCase).execute(CARD_NUMBER, CardStatus.BLOCKED);
            verifyNoMoreInteractions(updateCardStatusUseCase);
        }

        @Test
        @DisplayName("should return 204 when unblocking card")
        void shouldReturn204Unblock() throws Exception {
            mockMvc.perform(patch("/cartoes/{numeroCartao}", CARD_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "ACTIVE"}
                                    """))
                    .andExpect(status().isNoContent());

            verify(updateCardStatusUseCase).execute(CARD_NUMBER, CardStatus.ACTIVE);
            verifyNoMoreInteractions(updateCardStatusUseCase);
        }

        @Test
        @DisplayName("should return 400 when status is null")
        void shouldReturn400() throws Exception {
            mockMvc.perform(patch("/cartoes/{numeroCartao}", CARD_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateCardStatusUseCase);
        }

        @Test
        @DisplayName("should return 404 when card not found")
        void shouldReturn404() throws Exception {
            doThrow(new CardNotFoundException(CARD_NUMBER))
                    .when(updateCardStatusUseCase).execute(CARD_NUMBER, CardStatus.BLOCKED);

            mockMvc.perform(patch("/cartoes/{numeroCartao}", CARD_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "BLOCKED"}
                                    """))
                    .andExpect(status().isNotFound());

            verify(updateCardStatusUseCase).execute(CARD_NUMBER, CardStatus.BLOCKED);
        }
    }

    @Nested
    @DisplayName("GET /cartoes/{numeroCartao}/transacoes")
    class GetTransactionHistory {

        @Test
        @DisplayName("should return 200 and paginated history")
        void shouldReturn200() throws Exception {
            final TransactionEvent event = TransactionEvent.success(
                    CARD_NUMBER, new BigDecimal("100.00"), new BigDecimal("70.00"), new BigDecimal("30.00"));
            final Page<TransactionEvent> page = new PageImpl<>(List.of(event));
            final TransactionLogResponse response = new TransactionLogResponse(
                    CARD_NUMBER, TransactionStatus.SUCCESS,
                    "TRANSACAO_APROVADA", new BigDecimal("100.00"), new BigDecimal("70.00"),
                    new BigDecimal("30.00"), LocalDateTime.of(2026, 6, 20, 10, 0));

            when(getTransactionHistoryUseCase.execute(eq(CARD_NUMBER), isNull(), eq(0), eq(20))).thenReturn(page);
            when(transactionMapper.toResponse(event)).thenReturn(response);

            mockMvc.perform(get("/cartoes/{numeroCartao}/transacoes", CARD_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numeroCartao").value(CARD_NUMBER))
                    .andExpect(jsonPath("$.content[0].valor").value(30.00));

            verify(getTransactionHistoryUseCase).execute(eq(CARD_NUMBER), isNull(), eq(0), eq(20));
            verify(transactionMapper).toResponse(event);
        }

        @Test
        @DisplayName("should return 200 with empty page when no transactions")
        void shouldReturn200Empty() throws Exception {
            final Page<TransactionEvent> emptyPage = Page.empty();

            when(getTransactionHistoryUseCase.execute(eq(CARD_NUMBER), isNull(), eq(0), eq(20))).thenReturn(emptyPage);

            mockMvc.perform(get("/cartoes/{numeroCartao}/transacoes", CARD_NUMBER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(getTransactionHistoryUseCase).execute(eq(CARD_NUMBER), isNull(), eq(0), eq(20));
            verifyNoInteractions(transactionMapper);
        }

        @Test
        @DisplayName("should return 200 filtered by status")
        void shouldReturn200FilteredByStatus() throws Exception {
            final TransactionEvent event = TransactionEvent.success(
                    CARD_NUMBER, new BigDecimal("100.00"), new BigDecimal("70.00"), new BigDecimal("30.00"));
            final Page<TransactionEvent> page = new PageImpl<>(List.of(event));
            final TransactionLogResponse response = new TransactionLogResponse(
                    CARD_NUMBER, TransactionStatus.SUCCESS,
                    "TRANSACAO_APROVADA", new BigDecimal("100.00"), new BigDecimal("70.00"),
                    new BigDecimal("30.00"), LocalDateTime.of(2026, 6, 20, 10, 0));

            when(getTransactionHistoryUseCase.execute(eq(CARD_NUMBER), eq(TransactionStatus.SUCCESS), eq(0), eq(20)))
                    .thenReturn(page);
            when(transactionMapper.toResponse(event)).thenReturn(response);

            mockMvc.perform(get("/cartoes/{numeroCartao}/transacoes", CARD_NUMBER)
                            .param("status", "SUCCESS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numeroCartao").value(CARD_NUMBER))
                    .andExpect(jsonPath("$.content[0].status").value("SUCCESS"));

            verify(getTransactionHistoryUseCase).execute(eq(CARD_NUMBER), eq(TransactionStatus.SUCCESS), eq(0), eq(20));
            verify(transactionMapper).toResponse(event);
        }
    }
}
