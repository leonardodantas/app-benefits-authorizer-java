package com.leotech.benefits.authorizer.app.usecases;

import java.math.BigDecimal;

public interface GetBalanceUseCase {

    BigDecimal execute(String cardNumber);
}
