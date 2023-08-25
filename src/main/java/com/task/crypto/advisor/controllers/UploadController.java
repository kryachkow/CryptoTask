package com.task.crypto.advisor.controllers;

import com.task.crypto.advisor.aspects.annotations.RateLimited;
import com.task.crypto.advisor.dtos.UploadResponse;
import com.task.crypto.advisor.exceptions.CryptoValuesCsvValidationException;
import com.task.crypto.advisor.services.CsvUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * The `UploadController` class is responsible for handling data uploads via RESTful endpoints.
 * It provides a method to upload new data from a CSV file and receive a response about the upload process.
 **/
@RestController
@RequiredArgsConstructor
@Slf4j
@RateLimited
@RequestMapping("/upload")
public class UploadController {

    private final CsvUploadService csvUploadService;

    /**
     * Uploads new CSV data from the provided multipart file and returns an upload response.
     * Only users with AUTHORITY_WRITE could use this endpoint.
     *
     * @param file The multipart file containing the CSV data to be uploaded.
     *             csv constraints are:
     *             -> first row must consist column names such as 'timestamp symbol price'
     *             -> every row must consist 3 columns
     *             -> mills timestamp must be for pastime
     *             -> all rows(excluding first) must have same symbol column in second column
     *             -> third column must be int or double value
     * @return An upload response indicating the status and details of the upload process.
     * @throws CryptoValuesCsvValidationException                    if the file is missing, empty or violating any constrain.
     * @throws com.task.crypto.advisor.exceptions.UploadCsvException if any error occurs in uploading process.
     */
    @PostMapping("/upload-csv-data")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORITY_WRITE')")
    public UploadResponse uploadNewData(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CryptoValuesCsvValidationException("There is no file or file is empty");
        }
        return csvUploadService.uploadCsv(file);
    }
}
