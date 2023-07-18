package com.task.crypto.advisor.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
public class CryptoRecord {
    @CsvBindByName(column = "timestamp")
    private String timeStamp;
    @CsvBindByName(column = "symbol")
    private String symbol;
    @CsvBindByName(column = "price")
    private BigDecimal price;
    private LocalDate date;

    public void setLocalDateByTimestamp() {
        this.date = Instant.ofEpochMilli(Long.parseLong(timeStamp)).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
