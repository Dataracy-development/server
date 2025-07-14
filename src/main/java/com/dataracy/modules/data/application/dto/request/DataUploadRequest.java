package com.dataracy.modules.data.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DataUploadRequest(
        String title,
        Long topicId,
        Long dataSourceId,
        Long authorLevelId,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide
) {}
