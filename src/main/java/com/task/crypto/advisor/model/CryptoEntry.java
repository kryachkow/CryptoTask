package com.task.crypto.advisor.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@NoArgsConstructor
public class CryptoEntry {

    @CsvBindByName(column = "timestamp")
    @CsvBindByPosition(position = 0)

    private String timeStamp;
    @CsvBindByName(column = "symbol")
    @CsvBindByPosition(position = 1)
    private String symbol;
    @CsvBindByName(column = "price")
    @CsvBindByPosition(position = 2)
    private BigDecimal price;
    private LocalDate date;

    public CryptoEntry(String timeStamp, String symbol, BigDecimal price) {
        this.timeStamp = timeStamp;
        this.symbol = symbol;
        this.price = price;
        this.setLocalDateByTimestamp();
    }

    public void setLocalDateByTimestamp() {
        this.date = Instant.ofEpochMilli(Long.parseLong(timeStamp)).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
