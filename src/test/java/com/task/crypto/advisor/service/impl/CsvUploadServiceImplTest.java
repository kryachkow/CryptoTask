package com.task.crypto.advisor.service.impl;

import com.task.crypto.advisor.TestUtils;
import com.task.crypto.advisor.dto.UploadResponse;
import com.task.crypto.advisor.exception.CryptoValuesCsvValidationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CsvUploadServiceImplTest {


    @Autowired
    private CsvUploadServiceImpl csvUploadService;

    @AfterAll
    public static void tearDown() throws IOException {
        TestUtils.resetPricesFolder();
    }

    @ParameterizedTest(name = "Should throw the exception on corrupted data for {0} file")
    @MethodSource("getPathForException")
    @Order(1)
    void uploadFailedTest(MultipartFile multipartFile) {
        Assertions.assertThrows(CryptoValuesCsvValidationException.class, () -> csvUploadService.uploadCsv(multipartFile));

    }

    @ParameterizedTest(name = "Should create new file in catalog directory for new cryptos")
    @MethodSource("getCreatedFilesUploadArguments")
    @Order(2)
    void uploadSuccessfulNewFileCreated(int rowsAdded, MultipartFile file) {
        Assertions.assertEquals(
                UploadResponse
                        .builder()
                        .uploadStatus("Data was uploaded, new file created")
                        .rowsAdded(rowsAdded)
                        .build(), csvUploadService.uploadCsv(file)
        );
    }

    @ParameterizedTest(name = "Should merge file to existing without timestamp duplicates and rewriting for existing cryptos")
    @MethodSource("getMergedFilesUploadArguments")
    @Order(3)
    void uploadSuccessfulMergedToExistingFile(int rowsAdded, MultipartFile file) {
        Assertions.assertEquals(
                UploadResponse
                        .builder()
                        .uploadStatus("Data was merged to existing file")
                        .rowsAdded(rowsAdded)
                        .build(), csvUploadService.uploadCsv(file)
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should say that no rows added if all uploaded data already has timestamp duplicates")
    @SneakyThrows
    void uploadDuplicates() {
        File file = new File("src/test/resources/testCsv/duplicate/duplicate.csv");
        assertEquals(
                UploadResponse
                        .builder()
                        .uploadStatus("All rows in file have timestamp duplicates on server, no data uploaded")
                        .rowsAdded(0)
                        .build(),
                csvUploadService.uploadCsv(new MockMultipartFile(file.getName(), file.getName(), "text/csv", Files.readAllBytes(file.toPath())))
        );
    }


    private static Stream<Arguments> getPathForException() {
        File file = new File("src/test/resources/testCsv/validationException");
        return Arrays.stream(Objects.requireNonNull(file.listFiles())).map(f -> {
            try {
                return Arguments.of(new MockMultipartFile(f.getName(), f.getName(), "text/csv", Files.readAllBytes(f.toPath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Stream<Arguments> getCreatedFilesUploadArguments() {
        return getArgumentsForSuccessfulUploading("src/test/resources/testCsv/created");
    }

    private static Stream<Arguments> getMergedFilesUploadArguments() {
        return getArgumentsForSuccessfulUploading("src/test/resources/testCsv/merged");
    }

    private static Stream<Arguments> getArgumentsForSuccessfulUploading(String pathName) {
        File file = new File(pathName);
        return Arrays.stream(Objects.requireNonNull(file.listFiles())).map(f -> {
            try {
                return Arguments.of(Integer.parseInt(f.getName().replace(".csv", "")), new MockMultipartFile(f.getName(), f.getName(), "text/csv", Files.readAllBytes(f.toPath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}