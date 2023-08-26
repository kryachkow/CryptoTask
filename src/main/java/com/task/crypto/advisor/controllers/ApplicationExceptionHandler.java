package com.task.crypto.advisor.controllers;

import com.task.crypto.advisor.dtos.ErrorResponse;
import com.task.crypto.advisor.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(annotations = RestController.class)
public class ApplicationExceptionHandler {

    @ExceptionHandler({
            CryptoDataNotFoundException.class,
            CryptoStatisticException.class,
            CryptoValuesCsvValidationException.class,
            RateLimitException.class,
            UploadCsvException.class
    })
    public ResponseEntity<ErrorResponse> mapExceptionToErrorResponse(Exception e) {
        ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
        return ResponseEntity.status(annotation.value()).body(generateErrorResponseByResponseStatus(annotation));
    }

    private ErrorResponse generateErrorResponseByResponseStatus(ResponseStatus responseStatus) {
        return ErrorResponse.builder()
                .errorCode(responseStatus.value().value())
                .message(responseStatus.reason())
                .time(LocalDateTime.now())
                .build();
    }


}
