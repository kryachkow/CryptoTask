package com.task.crypto.advisor.service;

import com.task.crypto.advisor.model.CryptoRecord;

import java.time.LocalDate;
import java.util.Set;

public interface CryptoDataService {

    CryptoRecord getMinForCrypto(String crypto, LocalDate offsetDate);

    CryptoRecord getMaxForCrypto(String crypto, LocalDate offsetDate);

    CryptoRecord getLatestForCrypto(String crypto, LocalDate offsetDate);

    CryptoRecord getOldestForCrypto(String crypto, LocalDate offsetDate);

    Set<String> getCryptos();
}
