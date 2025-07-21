package com.dataracy.modules.dataset.adapter.web.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DataDetailWebResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide,
        String thumbnailUrl,
        int downloadCount,
        int recentWeekDownloadCount,
        Integer rowCount,
        Integer columnCount,
        String previewJson,
        LocalDateTime createdAt
) {}
