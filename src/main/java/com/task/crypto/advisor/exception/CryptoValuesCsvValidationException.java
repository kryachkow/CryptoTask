package com.task.crypto.advisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = """
        Review your csv file, appropriate file must meet the following conditions" +
                    "-> first row must consist column names such as 'timestamp symbol price'" +
                    "-> every row must consist 3 columns" +
                    "-> mills timestamp must be for past time " +
                    "-> all rows(excluding first) must have same symbol column in second column" +
                    "-> third column must be int or double value""")
public class CryptoValuesCsvValidationException extends RuntimeException {
    public CryptoValuesCsvValidationException(String message) {
        super(message);
    }

    public CryptoValuesCsvValidationException(String message, Exception cause) {
        super(message, cause);
    }
}
