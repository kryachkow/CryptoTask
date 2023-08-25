package com.task.crypto.advisor.entities.csv;

import java.time.LocalDate;


public record CryptoData(String crypto, CryptoEntry min, CryptoEntry max, CryptoEntry oldest, CryptoEntry newest,
                         LocalDate dateFrom, LocalDate dateTo) {

}
