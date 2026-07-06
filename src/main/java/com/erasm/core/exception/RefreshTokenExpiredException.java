package com.erasm.core.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
