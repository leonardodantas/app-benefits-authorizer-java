package com.leotech.benefits.authorizer.app.usecases.impl.transaction;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherHandlerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<TransactionEvent> eventCaptor;

    @Test
    @DisplayName("should publish TransactionEvent and set SUCCESS")
    void shouldPublishEventAndSetSuccess() {
        final Card card = Card.builder()
                .cardNumber("1234567890123456")
                .balance(new BigDecimal("100.00"))
                .build();

        final Transaction transaction = new Transaction("1234567890123456", "1234", new BigDecimal("30.00"));
        final TransactionContext context = new TransactionContext(transaction);
        context.setCard(card);

        final EventPublisherHandler handler = new EventPublisherHandler(eventPublisher);

        handler.doHandle(context);

        assertThat(context.getStatus()).isEqualTo(HandlerStatus.SUCCESS);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        final TransactionEvent event = eventCaptor.getValue();
        assertThat(event.cardNumber()).isEqualTo("1234567890123456");
        assertThat(event.previousBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(event.newBalance()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(event.timestamp()).isNotNull();

        verifyNoMoreInteractions(eventPublisher);
    }
}
