package com.task.crypto.advisor.service;

import com.task.crypto.advisor.model.CryptoData;
import com.task.crypto.advisor.model.CryptoEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * The `CryptoDataService` interface defines methods for retrieving cryptocurrency data.
 * It provides methods to obtain cryptocurrency data(set of crypto entries) within a specified date range,
 * retrieve a list of cryptocurrency entries, and obtain a set of available cryptocurrencies.
 */
public interface CryptoDataService {

    /**
     * Retrieves cryptocurrency(set of crypto entries) data for a specified cryptocurrency within a given date range.
     *
     * @param crypto   The name of the cryptocurrency.
     * @param dateFrom The starting date of the data range.
     * @param dateTo   The ending date of the data range.
     * @return CryptoData for the specified cryptocurrency.
     */
    CryptoData getCryptoData(String crypto, LocalDate dateFrom, LocalDate dateTo);

    /**
     * Retrieves a list of cryptocurrency entries for a specific cryptocurrency.
     *
     * @param crypto The name of the cryptocurrency.
     * @return List of CryptoEntry objects representing cryptocurrency data entries.
     */
    List<CryptoEntry> getCryptoEntries(String crypto);

    /**
     * Retrieves a set of available cryptocurrencies.
     *
     * @return Set of strings containing the names of available cryptocurrencies.
     */
    Set<String> getCryptos();
}
