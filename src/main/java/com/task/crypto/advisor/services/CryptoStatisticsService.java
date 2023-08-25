package com.task.crypto.advisor.services;

import com.task.crypto.advisor.dtos.CryptoStats;
import com.task.crypto.advisor.dtos.NormalizedRange;

import java.time.LocalDate;
import java.util.List;

/**
 * The `CryptoStatisticsService` interface outlines methods for retrieving cryptocurrency statistics.
 * It provides methods to obtain normalized ranges for all available cryptocurrencies, find the biggest
 * normalized range within a specified date range, and retrieve statistics for a particular cryptocurrency.
 */
public interface CryptoStatisticsService {
    /**
     * Retrieves a list of normalized ranges for all available cryptocurrencies.
     *
     * @return List of NormalizedRange objects containing normalized ranges for cryptocurrencies.
     */
    List<NormalizedRange> getNormalizedRangeForAllCryptos();

    /**
     * Retrieves the biggest normalized range within the specified date range.
     *
     * @param dateFrom The starting date of the date range.
     * @param dateTo   The ending date of the date range.
     * @return NormalizedRange representing the biggest normalized range within the specified date range.
     */
    NormalizedRange getBiggestNormalizedRangeForDate(LocalDate dateFrom, LocalDate dateTo);

    /**
     * Retrieves statistics for a specific cryptocurrency.
     *
     * @param crypto The name of the cryptocurrency.
     * @return CryptoStats containing statistics for the specified cryptocurrency.
     */
    CryptoStats getCryptoStatisticsByName(String crypto);

}
