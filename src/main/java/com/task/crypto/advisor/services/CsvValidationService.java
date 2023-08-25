package com.task.crypto.advisor.services;

import com.task.crypto.advisor.entities.csv.CryptoEntry;

import java.io.Reader;
import java.util.List;

/**
 * The `CsvValidationService` interface defines a method for reading and validating CSV data.
 * It provides a method to read data from a `Reader` and perform validation on the CSV content.
 */
public interface CsvValidationService {

    /**
     * Reads and validates CSV data from a provided `Reader`.
     *
     * @param reader The `Reader` object providing access to the CSV data.
     * @return A list of CryptoEntry objects representing validated cryptocurrency data.
     */
    List<CryptoEntry> readAndValidate(Reader reader);
}
