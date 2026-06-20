package com.leotech.benefits.authorizer.app.usecases.impl;

import com.leotech.benefits.authorizer.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @InjectMocks
    private CreateTransactionUseCaseImpl createTransactionUseCase;

    @Mock
    private TransactionExecutor transactionExecutor;

    @Test
    @DisplayName("should execute transaction via executor")
    void shouldExecuteTransaction() {
        final Transaction transaction = new Transaction("123", "raw-password", new BigDecimal("30.00"));

        createTransactionUseCase.execute(transaction);

        verify(transactionExecutor).execute(any(Transaction.class));
        verifyNoMoreInteractions(transactionExecutor);
    }
}
