package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaCardRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByCardNumber(String cardNumber);

    @Query(value = "SELECT * FROM cards WHERE card_number = ?1 FOR UPDATE", nativeQuery = true)
    Optional<CardEntity> findWithLockByCardNumber(String cardNumber);
}
