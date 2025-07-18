package com.dataracy.modules.data.application.dto.request;

import java.time.LocalDate;

public record DataUploadRequest(
        String title,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide
) {}
