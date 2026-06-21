package com.leotech.benefits.authorizer.infra.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter encoder = new BCryptPasswordEncoderAdapter();

    @Test
    @DisplayName("should encode and match password")
    void shouldEncodeAndMatch() {
        final String raw = "1234";
        final String encoded = encoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("should not match wrong password")
    void shouldNotMatchWrongPassword() {
        final String encoded = encoder.encode("1234");

        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }
}
