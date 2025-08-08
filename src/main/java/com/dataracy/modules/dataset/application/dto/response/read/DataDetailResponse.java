package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DataDetailResponse(
        Long id,
        String title,
        String username,
        String authorLabel,
        String occupationLabel,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide,
        String dataThumbnailUrl,
        Integer downloadCount,
        Integer rowCount,
        Integer columnCount,
        String previewJson,
        LocalDateTime createdAt
) {}
