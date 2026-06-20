package com.leotech.benefits.authorizer.domain.shared;

public abstract class CustomException extends RuntimeException {

    private final String message;

    protected CustomException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
