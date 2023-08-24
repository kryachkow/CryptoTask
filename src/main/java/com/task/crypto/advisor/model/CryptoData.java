package com.task.crypto.advisor.model;

import java.time.LocalDate;


public record CryptoData(String crypto, CryptoEntry min, CryptoEntry max, CryptoEntry oldest, CryptoEntry newest,
                         LocalDate dateFrom, LocalDate dateTo) {

}
