package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.domain.card.Card;
import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import com.leotech.benefits.authorizer.infra.mappers.CardInfraMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardRepositoryImplTest {

    @Mock
    private JpaCardRepository jpaCardRepository;

    @Mock
    private CardInfraMapper cardInfraMapper;

    @InjectMocks
    private CardRepositoryImpl repository;

    @Test
    @DisplayName("should find card by number")
    void shouldFindByCardNumber() {
        final CardEntity entity = CardEntity.builder().cardNumber("1234567890123456").build();
        final Card card = Card.builder().cardNumber("1234567890123456").build();

        when(jpaCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(entity));
        when(cardInfraMapper.toDomain(entity)).thenReturn(card);

        final Optional<Card> result = repository.findByCardNumber("1234567890123456");

        assertThat(result).isPresent();
        assertThat(result.get().cardNumber()).isEqualTo("1234567890123456");
        verify(jpaCardRepository).findByCardNumber("1234567890123456");
        verify(cardInfraMapper).toDomain(entity);
    }

    @Test
    @DisplayName("should find card with lock by number")
    void shouldFindWithLockByCardNumber() {
        final CardEntity entity = CardEntity.builder().cardNumber("1234567890123456").build();
        final Card card = Card.builder().cardNumber("1234567890123456").build();

        when(jpaCardRepository.findWithLockByCardNumber("1234567890123456")).thenReturn(Optional.of(entity));
        when(cardInfraMapper.toDomain(entity)).thenReturn(card);

        final Optional<Card> result = repository.findWithLockByCardNumber("1234567890123456");

        assertThat(result).isPresent();
        assertThat(result.get().cardNumber()).isEqualTo("1234567890123456");
        verify(jpaCardRepository).findWithLockByCardNumber("1234567890123456");
        verify(cardInfraMapper).toDomain(entity);
    }

    @Test
    @DisplayName("should save card")
    void shouldSaveCard() {
        final Card domainCard = Card.builder().cardNumber("1234567890123456").password("1234").balance(BigDecimal.TEN).build();
        final CardEntity entity = CardEntity.builder().cardNumber("1234567890123456").password("1234").balance(BigDecimal.TEN).build();
        final CardEntity savedEntity = CardEntity.builder().id(1L).cardNumber("1234567890123456").password("1234").balance(BigDecimal.TEN).build();
        final Card savedDomain = Card.builder().id(1L).cardNumber("1234567890123456").password("1234").balance(BigDecimal.TEN).build();

        when(cardInfraMapper.toEntity(domainCard)).thenReturn(entity);
        when(jpaCardRepository.save(entity)).thenReturn(savedEntity);
        when(cardInfraMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        final Card result = repository.save(domainCard);

        assertThat(result.cardNumber()).isEqualTo("1234567890123456");
        assertThat(result.id()).isEqualTo(1L);
        verify(cardInfraMapper).toEntity(domainCard);
        verify(jpaCardRepository).save(entity);
        verify(cardInfraMapper).toDomain(savedEntity);
        verifyNoMoreInteractions(jpaCardRepository, cardInfraMapper);
    }
}
