package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.app.repositories.TransactionLogRepository;
import com.leotech.benefits.authorizer.app.usecases.GetTransactionHistoryUseCase;
import com.leotech.benefits.authorizer.domain.transaction.TransactionEvent;
import com.leotech.benefits.authorizer.domain.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetTransactionHistoryUseCaseImpl implements GetTransactionHistoryUseCase {

    private final TransactionLogRepository transactionLogRepository;

    @Override
    public Page<TransactionEvent> execute(final String cardNumber, final TransactionStatus status,
                                           final int page, final int size) {
        if (Objects.nonNull(status)) {
            return transactionLogRepository.findByCardNumber(cardNumber, status, PageRequest.of(page, size));
        }
        return transactionLogRepository.findByCardNumber(cardNumber, PageRequest.of(page, size));
    }
}
