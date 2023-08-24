package com.task.crypto.advisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Couldn't obtain information for those criteria")
public class CryptoStatisticException extends RuntimeException {

    public CryptoStatisticException(String message) {
        super(message);
    }

    public CryptoStatisticException(String message, Exception cause) {
        super(message, cause);
    }

}
