package com.task.crypto.advisor.exception.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorEntity {
    private String message;
    private String cause;
}
