package com.task.crypto.advisor.service.impl;


import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;
import com.task.crypto.advisor.exception.CryptoStatisticException;
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

@Service
@RequiredArgsConstructor
public class CryptoStatisticsServiceImpl implements CryptoStatisticsService {

    private final CryptoDataService cryptoDataService;

    public List<NormalizedRange> getNormalizedRangeForAllCryptos() {
        return cryptoDataService
                .getCryptos()
                .stream()
                .map(crypto -> configureNormalizedRangeByName(crypto, LocalDate.now()))
                .sorted(Comparator.comparing(NormalizedRange::getNormalizedValue).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public NormalizedRange getBiggestNormalizedRangeForDate(LocalDate offsetDate) {
        return cryptoDataService
                .getCryptos()
                .stream()
                .map(crypto -> configureNormalizedRangeByName(crypto, offsetDate))
                .max(Comparator.comparing(NormalizedRange::getNormalizedValue))
                .orElseThrow(
                        () -> new CryptoStatisticException(String.format("Couldn't obtain highest normalized range for %s date", offsetDate.format(DateTimeFormatter.ISO_DATE)))
                );
    }

    public CryptoStats configureCryptoStatisticsByName(String crypto) {
        return CryptoStats.builder()
                .symbol(crypto.toUpperCase())
                .min(cryptoDataService.getMinForCrypto(crypto, LocalDate.now()).getPrice())
                .max(cryptoDataService.getMaxForCrypto(crypto, LocalDate.now()).getPrice())
                .oldest(cryptoDataService.getOldestForCrypto(crypto, LocalDate.now()).getPrice())
                .newest(cryptoDataService.getLatestForCrypto(crypto, LocalDate.now()).getPrice())
                .build();

    }


    private NormalizedRange configureNormalizedRangeByName(String cryptoName, LocalDate offsetDate) {
        return NormalizedRange
                .builder()
                .symbol(cryptoName.toUpperCase())
                .normalizedValue(calculateNormalizedRange(cryptoName, offsetDate))
                .offsetDate(offsetDate)
                .build();
    }

    private BigDecimal calculateNormalizedRange(String cryptoName, LocalDate offsetDate) {
        BigDecimal min = cryptoDataService.getMinForCrypto(cryptoName, offsetDate).getPrice();
        return cryptoDataService.getMaxForCrypto(cryptoName, offsetDate).getPrice().subtract(min).divide(min, 5, RoundingMode.HALF_UP);
    }
}
