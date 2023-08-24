package com.task.crypto.advisor.service.impl;


import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import com.task.crypto.advisor.model.CryptoData;
import com.task.crypto.advisor.service.CryptoDataService;
import com.task.crypto.advisor.service.CryptoStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The `CryptoStatisticsServiceImpl` class implements the `CryptoStatisticsService` interface
 * and provides methods for calculating and retrieving cryptocurrency statistics.
 * Class retrieves info from CryptoDataService bean.
 */
@Service
@RequiredArgsConstructor
public class CryptoStatisticsServiceImpl implements CryptoStatisticsService {

    private static final LocalDate FIRST_CRYPTO_INFO = LocalDate.of(2015, 11, 20);

    private final CryptoDataService cryptoDataService;

    /**
     * Retrieves a list of normalized ranges for all available cryptocurrencies.
     *
     * @return List of NormalizedRange objects representing normalized ranges for cryptocurrencies.
     */
    @Override
    public List<NormalizedRange> getNormalizedRangeForAllCryptos() {
        return cryptoDataService
                .getCryptos()
                .stream()
                .map(crypto -> configureNormalizedRangeByName(crypto, FIRST_CRYPTO_INFO, LocalDate.now()))
                .sorted(Comparator.comparing(NormalizedRange::getNormalizedValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the biggest normalized range within the specified date range.
     *
     * @param dateFrom The starting date of the date range.
     * @param dateTo   The ending date of the date range.
     * @return NormalizedRange representing the biggest normalized range within the specified date range.
     * @throws CryptoStatisticException if the highest normalized range cannot be obtained.
     */
    @Override
    public NormalizedRange getBiggestNormalizedRangeForDate(LocalDate dateFrom, LocalDate dateTo) {
        return cryptoDataService
                .getCryptos()
                .stream()
                .map(crypto -> configureNormalizedRangeByName(crypto, dateFrom, dateTo))
                .max(Comparator.comparing(NormalizedRange::getNormalizedValue))
                .orElseThrow(
                        () -> new CryptoStatisticException(String.format("Couldn't obtain highest normalized range for period  from %s to %s", dateFrom.format(DateTimeFormatter.ISO_DATE), dateTo.format(DateTimeFormatter.ISO_DATE)))
                );
    }

    /**
     * Retrieves statistics for a specific cryptocurrency.
     *
     * @param crypto The name of the cryptocurrency.
     * @return CryptoStats containing statistics for the specified cryptocurrency.
     */
    @Override
    public CryptoStats getCryptoStatisticsByName(String crypto) {
        CryptoData data = cryptoDataService.getCryptoData(crypto, FIRST_CRYPTO_INFO, LocalDate.now());
        return CryptoStats.builder()
                .symbol(crypto.toUpperCase())
                .min(data.min().getPrice())
                .max(data.max().getPrice())
                .oldest(data.oldest().getPrice())
                .newest(data.newest().getPrice())
                .build();

    }


    private NormalizedRange configureNormalizedRangeByName(String cryptoName, LocalDate dateFrom, LocalDate dateTo) {
        CryptoData data = cryptoDataService.getCryptoData(cryptoName, dateFrom, dateTo);
        return NormalizedRange
                .builder()
                .symbol(cryptoName.toUpperCase())
                .normalizedValue(calculateNormalizedRange(data))
                .dateFrom(data.oldest().getDate())
                .dateTo(data.newest().getDate())
                .build();
    }

    private BigDecimal calculateNormalizedRange(CryptoData cryptoData) {
        return cryptoData.max().getPrice().subtract(cryptoData.min().getPrice()).divide(cryptoData.min().getPrice(), 5, RoundingMode.HALF_UP);
    }
}
