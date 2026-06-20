package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @InjectMocks
    private CreateTransactionUseCaseImpl createTransactionUseCase;

    @Mock
    private TransactionExecutor transactionExecutor;

    @Mock
    private CardRepository cardRepository;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private static final String CARD_NUMBER = "123";
    private static final BigDecimal AMOUNT = new BigDecimal("30.00");
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal EXPECTED_BALANCE = new BigDecimal("70.00");

    @Test
    @DisplayName("should run chain, debit balance and save")
    void shouldRunChainDebitAndSave() {
        final Card card = Card.builder()
                .cardNumber(CARD_NUMBER)
                .password("encrypted")
                .balance(INITIAL_BALANCE)
                .build();

        final Transaction transaction = new Transaction(CARD_NUMBER, "raw-password", AMOUNT);

        when(transactionExecutor.execute(any(Transaction.class)))
                .thenReturn(card);

        createTransactionUseCase.execute(transaction);

        verify(transactionExecutor).execute(any(Transaction.class));
        verify(cardRepository).save(cardCaptor.capture());

        final Card savedCard = cardCaptor.getValue();
        assertThat(savedCard.cardNumber()).isEqualTo(CARD_NUMBER);
        assertThat(savedCard.password()).isEqualTo("encrypted");
        assertThat(savedCard.balance()).isEqualByComparingTo(EXPECTED_BALANCE);

        verifyNoMoreInteractions(cardRepository);
    }
}
