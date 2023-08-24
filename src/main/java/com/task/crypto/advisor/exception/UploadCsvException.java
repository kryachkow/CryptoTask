package com.task.crypto.advisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "File hasn't been uploaded")
public class UploadCsvException extends RuntimeException {

    public UploadCsvException(String message) {
        super(message);
    }

    public UploadCsvException(String message, Exception cause) {
        super(message, cause);
    }
}
