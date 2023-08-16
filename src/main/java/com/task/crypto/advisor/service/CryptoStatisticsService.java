package com.task.crypto.advisor.service;

import com.task.crypto.advisor.dto.CryptoStats;
import com.task.crypto.advisor.dto.NormalizedRange;

import java.time.LocalDate;
import java.util.List;

public interface CryptoStatisticsService {

    List<NormalizedRange> getNormalizedRangeForAllCryptos();

    NormalizedRange getBiggestNormalizedRangeForDate(LocalDate offsetDate);

    CryptoStats configureCryptoStatisticsByName(String crypto);

}
