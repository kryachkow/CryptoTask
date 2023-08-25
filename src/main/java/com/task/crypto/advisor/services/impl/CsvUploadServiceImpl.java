package com.task.crypto.advisor.services.impl;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.task.crypto.advisor.configurations.CsvStorageProperties;
import com.task.crypto.advisor.dtos.UploadResponse;
import com.task.crypto.advisor.entities.csv.CryptoEntry;
import com.task.crypto.advisor.exceptions.UploadCsvException;
import com.task.crypto.advisor.services.CryptoDataService;
import com.task.crypto.advisor.services.CsvUploadService;
import com.task.crypto.advisor.services.CsvValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The `CsvUploadServiceImpl` class is a service implementation responsible for uploading and managing CSV data.
 * It implements the `CsvUploadService` interface. Stores data in 'crypto.prices' application.properties path
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvUploadServiceImpl implements CsvUploadService {
    private static final String VALUES_SUFFIX = "_values.csv";
    private static final String RESOURCE_FILE_PREFIX = "file:";
    private static final String HEADER = "timestamp,symbol,price\n";
    private static final String DUPLICATE_INFO_UPLOAD_STATUS = "All rows in file have timestamp duplicates on server, no data uploaded";
    private static final String MERGED_TO_EXISTING_FILE = "Data was merged to existing file";
    private static final String DATA_UPLOADED = "Data was uploaded, new file created";
    private final CsvValidationService csvValidationService;
    private final CryptoDataService cryptoDataService;
    private final ResourceLoader resourceLoader;
    private final CsvStorageProperties csvStorageProperties;


    /**
     * Uploads and manages the CSV file containing cryptocurrency data.
     * File will be validated via CsvValidationService.
     * If there is no data in storage for uploaded cryptocurrency new file will be created.
     * If there existing data for uploaded cryptocurrency the file will be merged to old one.
     * In merging situation there will be no rewriting data by timestamp, all duplicates from new data will be erased.
     *
     * @param file The multipart file containing CSV data to be uploaded.
     * @return An UploadResponse indicating the status and details of the upload process.
     * @throws UploadCsvException if an unexpected exception occurs during the uploading process.
     */
    @CacheEvict(cacheNames = {"cryptos", "cryptoEntries", "cryptoData"}, allEntries = true)
    @Override
    public UploadResponse uploadCsv(MultipartFile file) {
        List<CryptoEntry> cryptoEntryList;
        String filePath;
        try {
            cryptoEntryList = csvValidationService
                    .readAndValidate(new InputStreamReader(file.getInputStream()));
            filePath = csvStorageProperties.targetDir() + "/" + obtainName(cryptoEntryList) + VALUES_SUFFIX;
            if (!shouldBeMerged(cryptoEntryList)) {
                return createNewFileAndUpload(cryptoEntryList, filePath);
            }
        } catch (IOException e) {
            log.error("The unexpected exception during csv uploading occurred", e);
            throw new UploadCsvException("An exception occurred in uploading process", e);
        }
        return removeDuplicateDataAndUpLoad(cryptoEntryList, filePath);
    }

    private UploadResponse removeDuplicateDataAndUpLoad(List<CryptoEntry> mergingList, String filePath) {
        List<CryptoEntry> serverEntries = cryptoDataService
                .getCryptoEntries(mergingList.get(0).getSymbol());
        Set<String> collect = serverEntries
                .stream()
                .map(CryptoEntry::getTimeStamp)
                .collect(Collectors.toSet());
        List<CryptoEntry> filtredList = mergingList.stream().filter(e -> !collect.contains(e.getTimeStamp())).toList();
        if (filtredList.size() == 0) {
            return UploadResponse.builder().uploadStatus(DUPLICATE_INFO_UPLOAD_STATUS).rowsAdded(0).build();
        }
        serverEntries.addAll(filtredList);
        serverEntries.sort(Comparator.comparing(CryptoEntry::getTimeStamp));
        uploadFile(serverEntries, filePath);
        return UploadResponse.builder().uploadStatus(MERGED_TO_EXISTING_FILE).rowsAdded(filtredList.size()).build();
    }

    private UploadResponse createNewFileAndUpload(List<CryptoEntry> uploadingList, String filePath) throws IOException {
        new File(filePath).createNewFile();
        uploadFile(uploadingList, filePath);
        return UploadResponse.builder().uploadStatus(DATA_UPLOADED).rowsAdded(uploadingList.size()).build();
    }

    private void uploadFile(List<CryptoEntry> cryptoEntries, String filePath) {
        Resource resource = resourceLoader.getResource(RESOURCE_FILE_PREFIX + filePath);
        try (Writer writer = new FileWriter(resource.getFile())) {
            writer.append(HEADER);
            StatefulBeanToCsv<CryptoEntry> beanToCsv = new StatefulBeanToCsvBuilder<CryptoEntry>(writer)
                    .withApplyQuotesToAll(false)
                    .withSeparator(',')
                    .build();

            beanToCsv.write(cryptoEntries);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error("Error occurred on uploading process exception -> {}", e.getMessage(), e);
            throw new UploadCsvException("Error occurred on uploading process", e);
        }
        log.info("Upload successful");
    }

    private boolean shouldBeMerged(List<CryptoEntry> cryptoEntries) {
        return cryptoDataService.getCryptos().contains(cryptoEntries.get(0).getSymbol());
    }

    private String obtainName(List<CryptoEntry> cryptoEntries) {
        return cryptoEntries.get(0).getSymbol();
    }
}
