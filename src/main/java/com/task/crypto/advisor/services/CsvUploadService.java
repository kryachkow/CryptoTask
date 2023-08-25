package com.task.crypto.advisor.services;

import com.task.crypto.advisor.dtos.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * The `CsvUploadService` interface defines a method for uploading CSV files and processing them.
 * It provides a method to upload a multipart file containing CSV data and receive an upload response.
 */
public interface CsvUploadService {
    /**
     * Uploads and processes a CSV file containing data.
     *
     * @param file The multipart file containing CSV data to be uploaded.
     * @return An UploadResponse indicating the status and details of the upload process.
     */
    UploadResponse uploadCsv(MultipartFile file);
}
