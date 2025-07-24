package com.dataracy.modules.dataset.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConnectedDataAssociatedWithProjectResponse(
        Long id,
        String title,
        String topicLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String thumbnailUrl,
        int downloadCount,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
