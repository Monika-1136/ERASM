package com.erasm.core.exception;

public class AllocationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public AllocationException(String message) {
        super(message);
    }
}
