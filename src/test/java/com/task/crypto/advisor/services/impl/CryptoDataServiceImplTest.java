package com.task.crypto.advisor.services.impl;

import com.task.crypto.advisor.entities.csv.CryptoData;
import com.task.crypto.advisor.entities.csv.CryptoEntry;
import com.task.crypto.advisor.exceptions.CryptoDataNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CryptoDataServiceImplTest {
    private final Set<String> cryptos = new HashSet<>(Set.of("TEST1", "TEST2"));

    @Autowired
    private CryptoDataServiceImpl cryptoDataService;


    @Test
    @Order(1)
    @DisplayName("Should return proper crypto names")
    void getCryptos() {
        assertEquals(cryptos, cryptoDataService.getCryptos());
    }

    @ParameterizedTest(name = "Service should return proper entries for {0} crypto name")
    @MethodSource("provideCryptoEntriesArguments")
    @Order(2)
    void getCryptoEntries(String crypto, List<CryptoEntry> expectedList) {
        assertEquals(expectedList, cryptoDataService.getCryptoEntries(crypto));
    }

    @Test
    @Order(3)
    @DisplayName("Should throw exception on nonexistent crypto name")
    void getCryptoEntriesOnNonexistentCryptoName() {
        assertThrows(CryptoDataNotFoundException.class, () -> cryptoDataService.getCryptoEntries("NOTHENAME"));
        assertThrows(CryptoDataNotFoundException.class, () -> cryptoDataService.getCryptoEntries("TEST3"));
    }


    @ParameterizedTest(name = "Service should return proper CryptoData for {0} crypto name and data boundaries")
    @MethodSource("provideCryptoDataArguments")
    @Order(4)
    void getCryptoData(String crypto, CryptoData expected) {
        assertEquals(expected, cryptoDataService.getCryptoData(crypto, LocalDate.now().minusYears(5), LocalDate.now()));
    }

    @Test
    @Order(3)
    @DisplayName("Should throw exception on unexciting crypto name")
    void getCryptoDataOnUnexcitingCryptoName() {
        assertThrows(CryptoDataNotFoundException.class, () -> cryptoDataService.getCryptoData("NOTHENAME", LocalDate.now().minusYears(5), LocalDate.now()));
    }

    private static Stream<Arguments> provideCryptoEntriesArguments() {
        return Stream.of(
                Arguments.of("TEST1", List.of(
                        new CryptoEntry("1641016800000", "TEST1", BigDecimal.valueOf(50)),
                        new CryptoEntry("1641063600000", "TEST1", BigDecimal.valueOf(100)),
                        new CryptoEntry("1641078000000", "TEST1", BigDecimal.valueOf(150))
                )),
                Arguments.of("TEST2", List.of(
                        new CryptoEntry("1641016800000", "TEST2", BigDecimal.valueOf(50)),
                        new CryptoEntry("1641063600000", "TEST2", BigDecimal.valueOf(75)),
                        new CryptoEntry("1641078000000", "TEST2", BigDecimal.valueOf(200))
                ))
        );
    }

    private static Stream<Arguments> provideCryptoDataArguments() {
        return Stream.of(
                Arguments.of("TEST1",
                        new CryptoData("TEST1",
                                new CryptoEntry("1641016800000", "TEST1", BigDecimal.valueOf(50)),
                                new CryptoEntry("1641078000000", "TEST1", BigDecimal.valueOf(150)),
                                new CryptoEntry("1641016800000", "TEST1", BigDecimal.valueOf(50)),
                                new CryptoEntry("1641078000000", "TEST1", BigDecimal.valueOf(150)),
                                Instant.ofEpochMilli(Long.parseLong("1641016800000")).atZone(ZoneId.systemDefault()).toLocalDate(),
                                Instant.ofEpochMilli(Long.parseLong("1641078000000")).atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                ),
                Arguments.of("TEST2",
                        new CryptoData("TEST2",
                                new CryptoEntry("1641016800000", "TEST2", BigDecimal.valueOf(50)),
                                new CryptoEntry("1641078000000", "TEST2", BigDecimal.valueOf(200)),
                                new CryptoEntry("1641016800000", "TEST2", BigDecimal.valueOf(50)),
                                new CryptoEntry("1641078000000", "TEST2", BigDecimal.valueOf(200)),
                                Instant.ofEpochMilli(Long.parseLong("1641016800000")).atZone(ZoneId.systemDefault()).toLocalDate(),
                                Instant.ofEpochMilli(Long.parseLong("1641078000000")).atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                )
        );
    }


}