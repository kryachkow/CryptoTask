package com.task.crypto.advisor.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoStats {
    private String symbol;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal oldest;
    private BigDecimal newest;
}
