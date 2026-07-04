package com.erasm.core.exception;

public class ResourceRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ResourceRequestException(String message) {
        super(message);
    }
}
