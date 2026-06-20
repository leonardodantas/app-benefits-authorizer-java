package com.leotech.benefits.authorizer.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        BigDecimal initialBalance
) {
}
