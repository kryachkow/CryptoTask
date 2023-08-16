package com.task.crypto.advisor.exception.handler;

import com.task.crypto.advisor.exception.CryptoDataNotFoundException;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import com.task.crypto.advisor.exception.model.ErrorEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

// TODO: 20.07.2023 move to Controller
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Map<Class<?>, String> ERROR_MESSAGE_MAP = new HashMap<>();

    static {
        ERROR_MESSAGE_MAP.put(CryptoDataNotFoundException.class, "There is no such crypto data please try another crypto");
        ERROR_MESSAGE_MAP.put(CryptoStatisticException.class, "There is no available for such crypto on that offset date");
        ERROR_MESSAGE_MAP.put(ConstraintViolationException.class, "Request is`nt valid, please check documentation");
    }

    @ExceptionHandler(value = {CryptoDataNotFoundException.class, CryptoStatisticException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleCryptoDataNotFoundException(Exception e) {
        return new ResponseEntity<>(ERROR_MESSAGE_MAP.get(e.getClass()),
                HttpStatus.BAD_REQUEST);
    }


}
