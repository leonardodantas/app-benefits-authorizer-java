package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.domain.card.CardStatus;
import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import com.leotech.benefits.authorizer.infra.mappers.CardInfraMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardRepositoryImplTest {

    @Mock
    private JpaCardRepository jpaCardRepository;

    @Spy
    private CardInfraMapper cardInfraMapper = Mappers.getMapper(CardInfraMapper.class);

    @InjectMocks
    private CardRepositoryImpl repository;

    @Test
    @DisplayName("should find card by number")
    void shouldFindByCardNumber() {
        final CardEntity entity = CardEntity.builder()
                .cardNumber("1234567890123456")
                .password("encrypted")
                .balance(new BigDecimal("100.00"))
                .status(CardStatus.ACTIVE)
                .build();

        when(jpaCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(entity));

        final Optional<Card> result = repository.findByCardNumber("1234567890123456");

        assertThat(result).isPresent();
        assertThat(result.get().cardNumber()).isEqualTo("1234567890123456");
        assertThat(result.get().password()).isEqualTo("encrypted");
        assertThat(result.get().balance()).isEqualByComparingTo(new BigDecimal("100.00"));
        verify(jpaCardRepository).findByCardNumber("1234567890123456");
    }

    @Test
    @DisplayName("should find card with lock by number")
    void shouldFindWithLockByCardNumber() {
        final CardEntity entity = CardEntity.builder()
                .cardNumber("1234567890123456")
                .password("encrypted")
                .balance(new BigDecimal("100.00"))
                .status(CardStatus.ACTIVE)
                .build();

        when(jpaCardRepository.findWithLockByCardNumber("1234567890123456")).thenReturn(Optional.of(entity));

        final Optional<Card> result = repository.findWithLockByCardNumber("1234567890123456");

        assertThat(result).isPresent();
        assertThat(result.get().cardNumber()).isEqualTo("1234567890123456");
        assertThat(result.get().password()).isEqualTo("encrypted");
        assertThat(result.get().balance()).isEqualByComparingTo(new BigDecimal("100.00"));
        verify(jpaCardRepository).findWithLockByCardNumber("1234567890123456");
    }

    @Test
    @DisplayName("should save card")
    void shouldSaveCard() {
        final Card domainCard = Card.builder()
                .cardNumber("1234567890123456")
                .password("1234")
                .balance(BigDecimal.TEN)
                .status(CardStatus.ACTIVE)
                .build();
        final CardEntity savedEntity = CardEntity.builder()
                .id(1L)
                .cardNumber("1234567890123456")
                .password("1234")
                .balance(BigDecimal.TEN)
                .status(CardStatus.ACTIVE)
                .build();

        when(jpaCardRepository.save(any(CardEntity.class))).thenReturn(savedEntity);

        final Card result = repository.save(domainCard);

        assertThat(result.cardNumber()).isEqualTo("1234567890123456");
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.TEN);
        verify(jpaCardRepository).save(any(CardEntity.class));
    }

    @Test
    @DisplayName("should find all cards with pagination")
    void shouldFindAll() {
        final CardEntity entity = CardEntity.builder()
                .cardNumber("1234567890123456")
                .password("encrypted")
                .balance(new BigDecimal("100.00"))
                .status(CardStatus.ACTIVE)
                .build();
        final PageRequest pageRequest = PageRequest.of(0, 20);
        final Page<CardEntity> entityPage = new PageImpl<>(List.of(entity));

        when(jpaCardRepository.findAll(pageRequest)).thenReturn(entityPage);

        final Page<Card> result = repository.findAll(pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("1234567890123456");
        assertThat(result.getContent().getFirst().balance()).isEqualByComparingTo(new BigDecimal("100.00"));
        verify(jpaCardRepository).findAll(pageRequest);
    }
}
