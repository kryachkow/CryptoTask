package com.task.crypto.advisor.services.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvValidationException;
import com.task.crypto.advisor.entities.csv.CryptoEntry;
import com.task.crypto.advisor.exceptions.CryptoValuesCsvValidationException;
import com.task.crypto.advisor.services.CsvValidationService;
import com.task.crypto.advisor.validators.CsvCryptoEntryRowValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The `CsvValidationServiceImpl` class implements the `CsvValidationService` interface and provides
 * methods for reading and validating CSV data.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CsvValidationServiceImpl implements CsvValidationService {

    private static final List<String> HEADERS_LIST = new ArrayList<>();

    static {
        HEADERS_LIST.add("timestamp");
        HEADERS_LIST.add("price");
        HEADERS_LIST.add("symbol");
    }

    /**
     * Reads and validates CSV data from a provided `Reader`.
     * Constraints
     * -> first row must consist column names such as 'timestamp symbol price'
     * -> every row must consist 3 columns
     * -> mills timestamp must be for pastime
     * -> all rows(excluding first) must have same symbol column in second column
     * -> third column must be int or double value
     *
     * @param reader The `Reader` object providing access to the CSV data.
     * @return A list of CryptoEntry objects representing validated cryptocurrency data.
     * @throws CryptoValuesCsvValidationException if an error occurs during CSV validation process.
     */
    @Override
    public List<CryptoEntry> readAndValidate(Reader reader) {
        log.info("Validation of new file is starting");
        List<CryptoEntry> cryptoEntries;
        try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withRowValidator(new CsvCryptoEntryRowValidator())
                .build()) {
            checkHeader(csvReader);
            cryptoEntries = configureCsvToBean(csvReader).parse();
        } catch (Exception e) {
            log.error("File validation error");
            throw new CryptoValuesCsvValidationException("Error occurred on csv validation process", e);
        }
        return cryptoEntries;
    }


    private void checkHeader(CSVReader csvReader) throws CsvValidationException, IOException {
        Set<String> collect = Arrays.stream(csvReader.readNext()).map(String::toLowerCase).collect(Collectors.toSet());
        if (!collect.containsAll(HEADERS_LIST)) {
            throw new CsvValidationException("Header mismatch");
        }
    }

    private CsvToBean<CryptoEntry> configureCsvToBean(CSVReader csvReader) {
        CsvToBean<CryptoEntry> csvToBean = new CsvToBean<>();
        csvToBean.setCsvReader(csvReader);
        MappingStrategy<CryptoEntry> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(CryptoEntry.class);
        csvToBean.setMappingStrategy(strategy);
        return csvToBean;
    }
}
