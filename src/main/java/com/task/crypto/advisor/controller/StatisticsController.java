package com.task.crypto.advisor.controller;

import com.task.crypto.advisor.aspect.annotation.RateLimited;
import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;
import com.task.crypto.advisor.exception.CryptoDataNotFoundException;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import com.task.crypto.advisor.service.CryptoStatisticsService;
import com.task.crypto.advisor.validation.annotation.DateValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Validated
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
    @GetMapping("/by-crypto/{crypto}")
    public CryptoStats getCryptoStatistics(@PathVariable("crypto") String crypto) {
        return cryptoStatisticsService.getCryptoStatisticsForName(crypto);
    }

    /**
     * Retrieves the highest normalized value for a specific date.
     *
     * @param date The date for which to retrieve the highest normalized value.
     * @return The highest normalized value for the specified date.
     * @throws javax.validation.ConstraintViolationException if the provided date is invalid
     * @throws CryptoStatisticException                      if there no data for such offset date
     */
    @GetMapping("/by-date/{date}")
    public NormalizedRange getHighestNormalizedValueCrypto(@PathVariable("date") @DateValidation String date) {
        return cryptoStatisticsService.getBiggestNormalizedRangeForDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
    }

}
