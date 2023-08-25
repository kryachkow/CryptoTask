package com.task.crypto.advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.crypto.advisor.dtos.CryptoStats;
import com.task.crypto.advisor.dtos.NormalizedRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static com.task.crypto.advisor.TestUtils.getUserToken;
import static com.task.crypto.advisor.TestUtils.performRequestWithToken;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Every controller method should throw 401 when unauthorized user try to reach the resource")
    @Order(1)
    void unauthorizedApiUsage() throws Exception {
        this.mockMvc
                .perform(get("/statistics/normalized-values"))
                .andExpect(status().isUnauthorized());
        this.mockMvc
                .perform(get("/statistics/crypto-statistics/randomCryptoName"))
                .andExpect(status().isUnauthorized());
        this.mockMvc
                .perform(get("/statistics/highest-normalized-range/2022-01-01/2022-01-30"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getSortedNormalizedValues() should return 200 OK and needed content")
    @Order(2)
    void getSortedNormalizedValues() throws Exception {
        performRequestWithToken(mockMvc, get("/statistics/normalized-values"), getUserToken(mockMvc))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].symbol", is("TEST2")))
                .andExpect(jsonPath("$[0].normalizedValue").value(BigDecimal.valueOf(3).setScale(1, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[0].dateFrom", is("2022-01-01")))
                .andExpect(jsonPath("$[0].dateTo", is("2022-01-02")))
                .andExpect(jsonPath("$[1].symbol", is("TEST1")))
                .andExpect(jsonPath("$[1].normalizedValue").value(BigDecimal.valueOf(2).setScale(1, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[1].dateFrom", is("2022-01-01")))
                .andExpect(jsonPath("$[1].dateTo", is("2022-01-02")))
                .andDo(print());
    }

    @ParameterizedTest(name = "Should return correct response for existing crypto")
    @MethodSource("getArgumentsForStatisticsCrypto")
    @Order(2)
    void getCryptoStatistics(MockHttpServletRequestBuilder requestBuilder, CryptoStats stats) throws Exception {
        performRequestWithToken(mockMvc, requestBuilder, getUserToken(mockMvc))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", aMapWithSize(5)))
                .andExpect(jsonPath("$.symbol", is(stats.getSymbol())))
                .andExpect(jsonPath("$.min").value(stats.getMin()))
                .andExpect(jsonPath("$.max").value(stats.getMax()))
                .andExpect(jsonPath("$.oldest").value(stats.getOldest()))
                .andExpect(jsonPath("$.newest").value(stats.getNewest()))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 404 code on nonexistent crypto")
    @Order(3)
    void nonexistentCryptoRequest() throws Exception {
        performRequestWithToken(mockMvc, get("/statistics/crypto-statistics/randomCryptoName"), getUserToken(mockMvc))
                .andExpect(status().isNotFound());
    }


    @ParameterizedTest(name = "Should return NormalizedRange.class for existing data")
    @MethodSource("getNormalizedRangeAndDateRequested")
    @Order(4)
    void getHighestNormalizedValueCrypto(MockHttpServletRequestBuilder requestBuilder, NormalizedRange range) throws Exception {
        performRequestWithToken(mockMvc, requestBuilder, getUserToken(mockMvc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(4)))
                .andExpect(jsonPath("$.symbol", is(range.getSymbol())))
                .andExpect(jsonPath("$.normalizedValue").value(range.getNormalizedValue()))
                .andExpect(jsonPath("$.dateFrom", is(range.getDateFrom().format(DateTimeFormatter.ISO_DATE))))
                .andExpect(jsonPath("$.dateTo", is(range.getDateTo().format(DateTimeFormatter.ISO_DATE))))
                .andDo(print());
        ;

    }

    @ParameterizedTest(name = "Should return error codes for corrupted input or nonexistent data for time period")
    @MethodSource("getCorruptedArgumentRequests")
    @Order(4)
    void getCorruptedArguments(MockHttpServletRequestBuilder requestBuilder, ResultMatcher status) throws Exception {
        performRequestWithToken(mockMvc, requestBuilder, getUserToken(mockMvc))
                .andExpect(status);
    }


    static Stream<Arguments> getArgumentsForStatisticsCrypto() {
        return Stream.of(
                Arguments.of(get("/statistics/crypto-statistics/TEST1"),
                        CryptoStats.builder().symbol("TEST1")
                                .min(BigDecimal.valueOf(50))
                                .max(BigDecimal.valueOf(150))
                                .oldest(BigDecimal.valueOf(50))
                                .newest(BigDecimal.valueOf(150))
                                .build()
                ),
                Arguments.of(get("/statistics/crypto-statistics/TEST2"),
                        CryptoStats.builder().symbol("TEST2")
                                .min(BigDecimal.valueOf(50))
                                .max(BigDecimal.valueOf(200))
                                .oldest(BigDecimal.valueOf(50))
                                .newest(BigDecimal.valueOf(200))
                                .build()
                )
        );
    }

    static Stream<Arguments> getNormalizedRangeAndDateRequested() {
        return Stream.of(
                Arguments.of(
                        get("/statistics/highest-normalized-range/2022-01-01/2022-01-02"),
                        NormalizedRange
                                .builder()
                                .symbol("TEST2")
                                .normalizedValue(BigDecimal.valueOf(3).setScale(1, RoundingMode.HALF_UP))
                                .dateFrom(LocalDate.of(2022, 1, 1))
                                .dateTo(LocalDate.of(2022, 1, 2)).build()
                ),
                Arguments.of(
                        get("/statistics/highest-normalized-range/2022-01-01/2022-01-01"),
                        NormalizedRange
                                .builder()
                                .symbol("TEST1")
                                .normalizedValue(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP))
                                .dateFrom(LocalDate.of(2022, 1, 1))
                                .dateTo(LocalDate.of(2022, 1, 1)).build()
                )
        );
    }

    static Stream<Arguments> getCorruptedArgumentRequests() {
        return Stream.of(
                Arguments.of(get("/statistics/highest-normalized-range/2023-01-01/2023-01-02"), status().isNotFound()),
                Arguments.of(get("/statistics/highest-normalized-range/01-01-asdasd/gsdfg-02-2022"), status().isBadRequest()),
                Arguments.of(get("/statistics/highest-normalized-range/2022-01-20/2022-01-02"), status().isNotFound())
        );
    }


}