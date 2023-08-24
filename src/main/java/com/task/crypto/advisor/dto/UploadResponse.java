package com.task.crypto.advisor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponse {
    private String uploadStatus;
    private int rowsAdded;
}
