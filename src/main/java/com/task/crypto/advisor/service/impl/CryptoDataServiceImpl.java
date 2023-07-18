package com.task.crypto.advisor.service.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import com.task.crypto.advisor.exception.CryptoDataNotFoundException;
import com.task.crypto.advisor.exception.CryptoStatisticException;
import com.task.crypto.advisor.model.CryptoRecord;
import com.task.crypto.advisor.service.CryptoDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class CryptoDataServiceImpl implements CryptoDataService {
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String OLDEST = "oldest";
    private static final String NEWEST = "newest";
    private static final String VALUES_SUFFIX = "_values.csv";
    @Value("${crypto.prices}")
    private final String csvPaths = "src/main/resources/prices";
    private final Map<String, List<CryptoRecord>> dataStorage = new HashMap<>();


    public CryptoDataServiceImpl() {
        log.info("Trying to get crypto data");
        initializeStorage();
    }

    @Cacheable(MIN)
    public CryptoRecord getMinForCrypto(String crypto, LocalDate offsetDate) {
        return getEdgeCryptoRecordByParams(crypto, offsetDate, Comparator.comparing(CryptoRecord::getPrice), MIN);

    }

    @Cacheable(MAX)
    public CryptoRecord getMaxForCrypto(String crypto, LocalDate offsetDate) {
        return getEdgeCryptoRecordByParams(crypto, offsetDate, Comparator.comparing(CryptoRecord::getPrice).reversed(), MAX);

    }

    @Cacheable(NEWEST)
    public CryptoRecord getLatestForCrypto(String crypto, LocalDate offsetDate) {
        return getEdgeCryptoRecordByParams(crypto, offsetDate, Comparator.comparing(CryptoRecord::getDate).reversed(), NEWEST);
    }

    @Cacheable(OLDEST)
    public CryptoRecord getOldestForCrypto(String crypto, LocalDate offsetDate) {
        return getEdgeCryptoRecordByParams(crypto, offsetDate, Comparator.comparing(CryptoRecord::getDate), OLDEST);
    }


    public Set<String> getCryptos() {
        return dataStorage.keySet();
    }


    private CryptoRecord getEdgeCryptoRecordByParams(String crypto, LocalDate offsetDate, Comparator<CryptoRecord> comparator, String neededCase) {
        return Optional.ofNullable(dataStorage.get(crypto.toUpperCase()))
                .orElseThrow(
                        () -> new CryptoDataNotFoundException(String.format("There is no data for %s crypto", crypto))
                )
                .stream()
                .filter(cryptoRecord -> cryptoRecord.getDate().isBefore(offsetDate) || cryptoRecord.getDate().isEqual(offsetDate))
                .min(comparator)
                .orElseThrow(
                        () -> new CryptoStatisticException(String.format("Couldn't obtain %s value for %s crypto for %s date", neededCase, crypto, offsetDate.format(DateTimeFormatter.ISO_DATE)))
                );
    }


    private void initializeStorage() {
        for (File file : Objects.requireNonNull(new File(csvPaths)
                .listFiles((dir, name) -> name.endsWith(VALUES_SUFFIX)))) {
            try (FileReader fileReader = new FileReader(file.getAbsolutePath())) {
                dataStorage.put(file.getName().replace(VALUES_SUFFIX, ""),
                        new CsvToBeanBuilder<CryptoRecord>(fileReader)
                                .withType(CryptoRecord.class)
                                .build()
                                .stream()
                                .peek(CryptoRecord::setLocalDateByTimestamp)
                                .toList());
            } catch (IOException e) {
                log.error("Couldn't obtain crypto info cause {}", e.getMessage());
                throw new CryptoDataNotFoundException(String.format("Couldn't obtain data by path %s ", csvPaths), e);
            }
        }
    }


}
