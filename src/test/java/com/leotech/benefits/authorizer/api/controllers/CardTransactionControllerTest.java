package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.handlers.ApiExceptionHandler;
import com.leotech.benefits.authorizer.api.mappers.TransactionMapper;
import com.leotech.benefits.authorizer.api.responses.TransactionLogResponse;
import com.leotech.benefits.authorizer.app.usecases.GetTransactionHistoryUseCase;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardTransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class CardTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    @MockBean
    private TransactionMapper transactionMapper;

    private static final String CARD_NUMBER = "6549873025634501";

    @Nested
    @DisplayName("GET /cartoes/{numeroCartao}/transacoes")
    class GetHistory {

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
