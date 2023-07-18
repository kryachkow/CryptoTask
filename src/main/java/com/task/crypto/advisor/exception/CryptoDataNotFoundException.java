package com.task.crypto.advisor.exception;

public class CryptoDataNotFoundException extends RuntimeException {

    public CryptoDataNotFoundException(String message) {
        super(message);
    }

    public CryptoDataNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
