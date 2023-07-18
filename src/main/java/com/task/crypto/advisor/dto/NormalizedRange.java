package com.task.crypto.advisor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class NormalizedRange {
    private String symbol;
    private BigDecimal normalizedValue;
    private LocalDate offsetDate;
}
