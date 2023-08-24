package com.task.crypto.advisor.service.impl;

import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class CryptoStatisticsServiceImplTest {

    @Autowired
    private CryptoStatisticsServiceImpl cryptoStatisticsService;


    @Test
    @Order(1)
    @DisplayName("Method getNormalizedRangeForAllCryptos() should return proper list with decreasing normalized range")
    void getNormalizedRangeForAllCryptos() {
        List<NormalizedRange> normalizedRangeForAllCryptos = cryptoStatisticsService.getNormalizedRangeForAllCryptos();
        Assertions.assertEquals(
                NormalizedRange
                        .builder()
                        .symbol("TEST2")
                        .normalizedValue(BigDecimal.valueOf(3).setScale(5, RoundingMode.HALF_UP))
                        .dateFrom(LocalDate.of(2022, 1, 1))
                        .dateTo(LocalDate.of(2022, 1, 2)).build(),
                normalizedRangeForAllCryptos.get(0));
        Assertions.assertEquals(
                NormalizedRange
                        .builder()
                        .symbol("TEST1")
                        .normalizedValue(BigDecimal.valueOf(2).setScale(5, RoundingMode.HALF_UP))
                        .dateFrom(LocalDate.of(2022, 1, 1))
                        .dateTo(LocalDate.of(2022, 1, 2)).build(),
                normalizedRangeForAllCryptos.get(1)
        );
    }

    @ParameterizedTest(name = "Should return proper statistics for {0}")
    @Order(2)
    @MethodSource("getStatisticsByNameArguments")
    void getCryptoStatisticsByName(String crypto, CryptoStats expected) {
        Assertions.assertEquals(expected, cryptoStatisticsService.getCryptoStatisticsByName(crypto));
    }


    @ParameterizedTest(name = "Should return proper normalized range for date period from {0} to {1}")
    @Order(3)
    @MethodSource("getNormalizedRangeForDateArguments")
    void getBiggestNormalizedRangeForDate(LocalDate dateFrom, LocalDate dateTo, NormalizedRange expected) {
        Assertions.assertEquals(expected, cryptoStatisticsService.getBiggestNormalizedRangeForDate(dateFrom, dateTo));
    }

    @Test
    @Order(4)
    @DisplayName("getBiggestNormalizedRangeForDate() Should return CryptoStatisticsException if there is no data for given time period")
    void exceptionTest() {
        Assertions.assertThrows(CryptoStatisticException.class, () -> cryptoStatisticsService.getBiggestNormalizedRangeForDate(LocalDate.of(2022, 3, 5), LocalDate.of(2022, 4, 5)));
    }

    static Stream<Arguments> getStatisticsByNameArguments() {
        return Stream.of(
                Arguments.of("TEST1",
                        CryptoStats.builder().symbol("TEST1")
                                .min(BigDecimal.valueOf(50))
                                .max(BigDecimal.valueOf(150))
                                .oldest(BigDecimal.valueOf(50))
                                .newest(BigDecimal.valueOf(150))
                                .build()
                ),
                Arguments.of("TEST2",
                        CryptoStats.builder().symbol("TEST2")
                                .min(BigDecimal.valueOf(50))
                                .max(BigDecimal.valueOf(200))
                                .oldest(BigDecimal.valueOf(50))
                                .newest(BigDecimal.valueOf(200))
                                .build()
                )
        );
    }

    static Stream<Arguments> getNormalizedRangeForDateArguments() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2022, 1, 1),
                        LocalDate.of(2022, 1, 2),
                        NormalizedRange
                                .builder()
                                .symbol("TEST2")
                                .normalizedValue(BigDecimal.valueOf(3).setScale(5, RoundingMode.HALF_UP))
                                .dateFrom(LocalDate.of(2022, 1, 1))
                                .dateTo(LocalDate.of(2022, 1, 2)).build()
                ),
                Arguments.of(
                        LocalDate.of(2022, 1, 1),
                        LocalDate.of(2022, 1, 1),
                        NormalizedRange
                                .builder()
                                .symbol("TEST1")
                                .normalizedValue(BigDecimal.valueOf(1).setScale(5, RoundingMode.HALF_UP))
                                .dateFrom(LocalDate.of(2022, 1, 1))
                                .dateTo(LocalDate.of(2022, 1, 1)).build()
                )
        );
    }


}