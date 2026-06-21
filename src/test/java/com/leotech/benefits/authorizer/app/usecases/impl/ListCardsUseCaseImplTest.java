package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.CardRepository;
import com.leotech.benefits.authorizer.domain.card.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCardsUseCaseImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private ListCardsUseCaseImpl useCase;

    @Test
    @DisplayName("should return paginated cards")
    void shouldReturnPaginatedCards() {
        final Card card = Card.builder()
                .cardNumber("1234567890123456")
                .balance(new BigDecimal("500.00"))
                .build();
        final Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        final Page<Card> result = useCase.execute(0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("1234567890123456");
        verify(cardRepository).findAll(PageRequest.of(0, 20));
        verifyNoMoreInteractions(cardRepository);
    }
}
