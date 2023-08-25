package com.task.crypto.advisor;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TestUtils {


    private static final String INITIAL_DIRECTORY = "temporary-resources";
    private static final String COPY_DIRECTORY = "src/test/resources/copy";

    private TestUtils() {
    }

    public static void resetPricesFolder() throws IOException {
        File initialDirectory = new File(INITIAL_DIRECTORY);
        FileUtils.cleanDirectory(initialDirectory);
        for (File file : Objects.requireNonNull(new File(COPY_DIRECTORY).listFiles())) {
            Files.copy(file.toPath(), initialDirectory.toPath().resolve(file.toPath().getFileName()));
        }
    }

    public static String getUserToken(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .with(httpBasic("testUser", "12345678")))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    public static String getAdminToken(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .with(httpBasic("testAdmin", "12345678")))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    public static ResultActions performRequestWithToken(MockMvc mockMvc, MockHttpServletRequestBuilder requestBuilder, String token) throws Exception {
        return mockMvc.perform(requestBuilder.header("Authorization", "Bearer " + token));
    }

}
