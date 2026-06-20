package com.leotech.benefits.authorizer.api.controllers;

import com.leotech.benefits.authorizer.api.handlers.ApiExceptionHandler;
import com.leotech.benefits.authorizer.api.mappers.CardMapper;
import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import com.leotech.benefits.authorizer.app.usecases.CreateCardUseCase;
import com.leotech.benefits.authorizer.app.usecases.GetBalanceUseCase;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardAlreadyExistsException;
import com.leotech.benefits.authorizer.domain.card.CardNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private CardMapper cardMapper;

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
}
