package com.leotech.benefits.authorizer.infra.repositories;

import com.leotech.benefits.authorizer.infra.entities.CardEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface JpaCardRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByCardNumber(String cardNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findWithLockByCardNumber(String cardNumber);
}
