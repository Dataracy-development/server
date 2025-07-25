package com.dataracy.modules.dataset.application.dto.request;

import java.time.LocalDate;

public record DataModifyRequest(
        String title,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide
) {}
