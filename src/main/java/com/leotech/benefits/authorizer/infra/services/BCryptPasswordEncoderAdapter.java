package com.leotech.benefits.authorizer.infra.services;

import com.leotech.benefits.authorizer.app.services.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(final String rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(final String rawPassword, final String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
