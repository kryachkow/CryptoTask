package com.task.crypto.advisor.services.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.task.crypto.advisor.configurations.CsvStorageProperties;
import com.task.crypto.advisor.entities.csv.CryptoData;
import com.task.crypto.advisor.entities.csv.CryptoEntry;
import com.task.crypto.advisor.exceptions.CryptoDataNotFoundException;
import com.task.crypto.advisor.exceptions.CryptoStatisticException;
import com.task.crypto.advisor.services.CryptoDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The `CryptoDataServiceImpl` class implements the `CryptoDataService` interface and provides
 * methods to retrieve and process cryptocurrency data from csv data storage in application.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CryptoDataServiceImpl implements CryptoDataService {

    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String OLDEST = "oldest";
    private static final String NEWEST = "newest";
    private static final String VALUES_SUFFIX = "_values.csv";
    private final CsvStorageProperties csvStorageProperties;


    /**
     * Retrieves cryptocurrency data for the specified cryptocurrency within a given date range.
     *
     * @param crypto   The name of the cryptocurrency.
     * @param dateFrom The starting date of the data range.
     * @param dateTo   The ending date of the data range.
     * @return CryptoData containing various statistics and information about the cryptocurrency.
     * @throws CryptoDataNotFoundException if there is no data available for the specified cryptocurrency.
     */
    @Cacheable("cryptoData")
    @Override
    public CryptoData getCryptoData(String crypto, LocalDate dateFrom, LocalDate dateTo) {
        List<CryptoEntry> cryptoRecords = getCryptoEntries(crypto);
        CryptoEntry oldestForCrypto = getOldestForCrypto(crypto, cryptoRecords, dateFrom, dateTo);
        CryptoEntry newestForCrypto = getNewestForCrypto(crypto, cryptoRecords, dateFrom, dateTo);
        return new CryptoData(
                crypto,
                getMinForCrypto(crypto, cryptoRecords, dateFrom, dateTo),
                getMaxForCrypto(crypto, cryptoRecords, dateFrom, dateTo),
                oldestForCrypto,
                newestForCrypto,
                oldestForCrypto.getDate(),
                newestForCrypto.getDate()
        );
    }


    /**
     * Retrieves a set of available cryptocurrencies from the provided CSV paths.
     *
     * @return A set of strings representing the names of available cryptocurrencies.
     */
    @Cacheable("cryptos")
    @Override
    public Set<String> getCryptos() {
        return Arrays.stream(Objects.requireNonNull(new File(csvStorageProperties.targetDir()).listFiles((dir, name) -> name.endsWith(VALUES_SUFFIX))))
                .map(file -> file.getName().replace(VALUES_SUFFIX, ""))
                .collect(Collectors.toSet());
    }


    /**
     * Retrieves a list of cryptocurrency entries for a specific cryptocurrency.
     *
     * @param crypto The name of the cryptocurrency.
     * @return List of CryptoEntry objects representing cryptocurrency data entries.
     * @throws CryptoDataNotFoundException if there is no data available for the specified cryptocurrency.
     */
    @Cacheable("cryptoEntries")
    @Override
    public List<CryptoEntry> getCryptoEntries(String crypto) {
        List<CryptoEntry> returnList;
        try (InputStreamReader inputStreamReader = getInputStreamReader(crypto)) {
            MappingStrategy<CryptoEntry> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CryptoEntry.class);
            returnList = new ArrayList<>(new CsvToBeanBuilder<CryptoEntry>(inputStreamReader)
                    .withType(CryptoEntry.class)
                    .withMappingStrategy(strategy)
                    .build()
                    .stream()
                    .peek(CryptoEntry::setLocalDateByTimestamp)
                    .toList());

        } catch (IOException e) {
            log.error("Couldn't obtain crypto info cause {}", e.getMessage());
            throw new CryptoDataNotFoundException(String.format("There is no data for crypto %s", crypto), e);
        }
        return returnList;
    }

    private InputStreamReader getInputStreamReader(String crypto) throws IOException {
        return new InputStreamReader(Files.newInputStream(Paths.get(csvStorageProperties.targetDir() + "/" + crypto.toUpperCase() + VALUES_SUFFIX)));
    }

    private CryptoEntry getEdgeCryptoEntryByParams(String crypto, List<CryptoEntry> cryptoEntries, LocalDate dateFrom, LocalDate dateTo, Comparator<CryptoEntry> comparator, String neededCase) {
        return Optional.of(
                        cryptoEntries
                                .stream()
                                .filter(cryptoEntry -> !(cryptoEntry.getDate().isBefore(dateFrom) || cryptoEntry.getDate().isAfter(dateTo)))
                                .min(comparator))
                .get()
                .orElseThrow(
                        () -> new CryptoStatisticException(String.format("Couldn't obtain %s value for %s crypto for %s to %s period", neededCase, crypto, dateFrom.format(DateTimeFormatter.ISO_DATE), dateTo.format(DateTimeFormatter.ISO_DATE))));
    }


    private CryptoEntry getMinForCrypto(String crypto, List<CryptoEntry> obtainedList, LocalDate dateFrom, LocalDate dateTo) {
        return getEdgeCryptoEntryByParams(crypto, obtainedList, dateFrom, dateTo, Comparator.comparing(CryptoEntry::getPrice), MIN);

    }

    private CryptoEntry getMaxForCrypto(String crypto, List<CryptoEntry> obtainedList, LocalDate dateFrom, LocalDate dateTo) {
        return getEdgeCryptoEntryByParams(crypto, obtainedList, dateFrom, dateTo, Comparator.comparing(CryptoEntry::getPrice).reversed(), MAX);

    }

    private CryptoEntry getNewestForCrypto(String crypto, List<CryptoEntry> obtainedList, LocalDate dateFrom, LocalDate dateTo) {
        return getEdgeCryptoEntryByParams(crypto, obtainedList, dateFrom, dateTo, Comparator.comparing(CryptoEntry::getDate).reversed(), NEWEST);
    }

    private CryptoEntry getOldestForCrypto(String crypto, List<CryptoEntry> obtainedList, LocalDate dateFrom, LocalDate dateTo) {
        return getEdgeCryptoEntryByParams(crypto, obtainedList, dateFrom, dateTo, Comparator.comparing(CryptoEntry::getDate), OLDEST);
    }


}
