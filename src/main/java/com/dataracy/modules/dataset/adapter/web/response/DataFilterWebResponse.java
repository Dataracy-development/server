package com.dataracy.modules.dataset.adapter.web.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DataFilterWebResponse(
        Long id,
        String title,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String thumbnailUrl,
        int downloadCount,
        int recentWeekDownloadCount,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
