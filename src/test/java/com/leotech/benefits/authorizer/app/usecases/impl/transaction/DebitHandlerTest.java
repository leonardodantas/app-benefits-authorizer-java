package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebitHandlerTest {

    @Mock
    private CardRepository cardRepository;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    @Test
    @DisplayName("should debit balance and save")
    void shouldDebitAndSave() {
        final Card card = Card.builder()
                .cardNumber("123")
                .password("encrypted")
                .balance(new BigDecimal("100.00"))
                .build();

        final Transaction transaction = new Transaction("123", "senha", new BigDecimal("30.00"));
        final TransactionContext context = new TransactionContext(transaction);
        context.setCard(card);

        final DebitHandler handler = new DebitHandler(cardRepository);

        handler.doHandle(context);

        assertThat(context.getStatus()).isEqualTo(HandlerStatus.SUCCESS);
        verify(cardRepository).save(cardCaptor.capture());

        final Card saved = cardCaptor.getValue();
        assertThat(saved.cardNumber()).isEqualTo("123");
        assertThat(saved.balance()).isEqualByComparingTo(new BigDecimal("70.00"));
        verifyNoMoreInteractions(cardRepository);
    }
}
