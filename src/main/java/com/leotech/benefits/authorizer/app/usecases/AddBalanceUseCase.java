package com.leotech.benefits.authorizer.app.usecases;

import java.math.BigDecimal;

public interface AddBalanceUseCase {

    void execute(String cardNumber, BigDecimal amount);
}
