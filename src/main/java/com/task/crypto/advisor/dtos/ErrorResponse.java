package com.task.crypto.advisor.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int errorCode;
    private String message;
    private LocalDateTime time;
}
