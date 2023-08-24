package com.task.crypto.advisor.controller;

import com.task.crypto.advisor.aspect.annotation.RateLimited;
import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;
import com.task.crypto.advisor.exception.CryptoDataNotFoundException;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import com.task.crypto.advisor.service.CryptoStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
/**
 * The `StatisticsController` class handles RESTful endpoints related to cryptocurrency statistics.
 * It provides methods to retrieve normalized ranges, specific cryptocurrency statistics, and highest
 * normalized values within a given time period.
 * The base request mapping for this controller is "/statistics".
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@RateLimited
public class StatisticsController {

    private final CryptoStatisticsService cryptoStatisticsService;

    /**
     * Retrieves a list of normalized ranges for all cryptocurrencies, sorted in descending order
     *
     * @return NormalizedRange of all available cryptos
     */
    @GetMapping("/normalized-values")
    public List<NormalizedRange> getSortedNormalizedValues() {
        return cryptoStatisticsService.getNormalizedRangeForAllCryptos();
    }

    /**
     * Retrieves the statistics for a specific cryptocurrency.
     *
     * @param crypto The name of the cryptocurrency.
     * @return The statistics for the specified cryptocurrency.
     * @throws CryptoDataNotFoundException if there is no data for such crypto name
     */
    @GetMapping("/crypto-statistics/{crypto}")
    public CryptoStats getCryptoStatistics(@PathVariable("crypto") String crypto) {
        return cryptoStatisticsService.getCryptoStatisticsByName(crypto);
    }

    /**
     * Retrieves the highest normalized value for a specific time period.
     *
     * @param dateFrom First date of time period in MM-dd-yyyy format.
     * @param dateTo Second date of time period in MM-dd-yyyy format.
     * @return The highest normalized value for the specified date.
     * @throws CryptoStatisticException  if there no data for such date period
     */
    @GetMapping("/highest-normalized-range/{dateFrom}/{dateTo}")
    public NormalizedRange getHighestNormalizedValueCrypto(@PathVariable("dateFrom") @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate dateFrom, @PathVariable @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate dateTo) {
        if (dateTo.isBefore(dateFrom) || dateFrom.isAfter(LocalDate.now())) {
            throw new CryptoStatisticException("Can`t obtain statistics for inappropriate date period");
        }
        return cryptoStatisticsService.getBiggestNormalizedRangeForDate(dateFrom, dateTo);
    }

}
