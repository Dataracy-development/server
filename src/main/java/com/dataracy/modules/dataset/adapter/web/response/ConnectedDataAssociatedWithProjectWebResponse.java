package com.dataracy.modules.dataset.adapter.web.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConnectedDataAssociatedWithProjectWebResponse(
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
