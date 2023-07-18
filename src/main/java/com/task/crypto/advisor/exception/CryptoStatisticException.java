package com.task.crypto.advisor.exception;

public class CryptoStatisticException extends RuntimeException {

    public CryptoStatisticException(String message) {
        super(message);
    }

    public CryptoStatisticException(String message, Exception cause) {
        super(message, cause);
    }

}
