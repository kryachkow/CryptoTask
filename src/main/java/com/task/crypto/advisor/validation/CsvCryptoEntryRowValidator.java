package com.task.crypto.advisor.validation;

import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.validators.RowValidator;

import java.time.Instant;
import java.util.Objects;

public class CsvCryptoEntryRowValidator implements RowValidator {

    private static final String REGEX_PRICE = "^\\d+.?\\d*$";
    private static final String REGEX_TIMESTAMP = "^\\d+";
    private static final String ERROR_MESSAGE = ".csv validation was failed";

    private String cryptoName;
    private boolean firstRowValidated = false;

    @Override
    public boolean isValid(String[] row) {
        if (!firstRowValidated) {
            firstRowValidated = true;
            return true;
        }
        if (cryptoName == null) {
            cryptoName = row[1];
        }
        return row != null
                && row.length == 3
                && row[0].matches(REGEX_TIMESTAMP)
                && checkDateValidity(row[0])
                && Objects.equals(row[1], cryptoName)
                && row[2].matches(REGEX_PRICE);
    }

    @Override
    public void validate(String[] row) throws CsvValidationException {
        if (!isValid(row)) {
            throw new CsvValidationException(ERROR_MESSAGE);
        }
    }

    private boolean checkDateValidity(String milsTimestamp) {
        return Instant.ofEpochMilli(Long.parseLong(milsTimestamp)).isBefore(Instant.now());
    }
}
