package com.task.crypto.advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.crypto.advisor.TestUtils;
import com.task.crypto.advisor.dtos.UploadResponse;
import com.task.crypto.advisor.services.impl.CryptoDataServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static com.task.crypto.advisor.TestUtils.getAdminToken;
import static com.task.crypto.advisor.TestUtils.getUserToken;
import static com.task.crypto.advisor.TestUtils.performRequestWithToken;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CryptoDataServiceImpl cryptoDataService;

    @AfterAll
    public static void tearDown() throws IOException {
        TestUtils.resetPricesFolder();
    }

    @Test
    @DisplayName("Should throw 401 on unauthenticated request")
    @Order(1)
    void unauthenticatedRequest() throws Exception {
        this.mockMvc
                .perform(post("/upload/upload-csv-data"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should throw 403 on user request(without AUTHORITY_WRITE)")
    @Order(2)
    void unauthorisedRequest() throws Exception {
        File fileCreated = new File("src/test/resources/testCsv/created/3.csv");
        performRequestWithToken(mockMvc,
                multipart(HttpMethod.POST, "/upload/upload-csv-data")
                        .file(new MockMultipartFile("file", fileCreated.getName(), "text/csv", Files.readAllBytes(fileCreated.toPath()))),
                getUserToken(mockMvc))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "Should correctly save new info")
    @MethodSource("getCorrectSaveRequestParams")
    @Order(3)
    void correctSaveRequest(MockMultipartFile multipartFile, UploadResponse expected, String crypto, int newExpectedEntriesSize) throws Exception {
        performRequestWithToken(mockMvc,
                multipart(HttpMethod.POST, "/upload/upload-csv-data").file(multipartFile),
                getAdminToken(mockMvc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.uploadStatus", is(expected.getUploadStatus())))
                .andExpect(jsonPath("$.rowsAdded", is(expected.getRowsAdded())));

        assertEquals(newExpectedEntriesSize, cryptoDataService.getCryptoEntries(crypto).size());
    }

    @ParameterizedTest(name = "Invalid file test")
    @MethodSource("getInvalidFileRequestParam")
    @Order(4)
    void invalidFileRequest(MockMultipartFile multipartFile, ResultMatcher status) throws Exception {
        performRequestWithToken(mockMvc,
                multipart(HttpMethod.POST, "/upload/upload-csv-data").file(multipartFile),
                getAdminToken(mockMvc))
                .andExpect(status);
    }

    private static Stream<Arguments> getCorrectSaveRequestParams() throws IOException {
        File fileCreated = new File("src/test/resources/testCsv/created/3.csv");
        File fileMerged = new File("src/test/resources/testCsv/merged/3.csv");
        return Stream.of(
                Arguments.of(
                        new MockMultipartFile("file", fileCreated.getName(), "text/csv", Files.readAllBytes(fileCreated.toPath())),
                        UploadResponse.builder().uploadStatus("Data was uploaded, new file created").rowsAdded(3).build(),
                        "TEST3",
                        3),
                Arguments.of(
                        new MockMultipartFile("file", fileMerged.getName(), "text/csv", Files.readAllBytes(fileMerged.toPath())),
                        UploadResponse.builder().uploadStatus("Data was merged to existing file").rowsAdded(3).build(),
                        "TEST2",
                        6)
        );
    }

    private static Stream<Arguments> getInvalidFileRequestParam() {
        File file = new File("src/test/resources/testCsv/validationException");
        return Arrays.stream(Objects.requireNonNull(file.listFiles())).map(f -> {
            try {
                return Arguments.of(new MockMultipartFile("file", f.getName(), "text/csv", Files.readAllBytes(f.toPath())), status().isBadRequest());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}