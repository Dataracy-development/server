package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param title
 * @param topicLabel
 * @param dataTypeLabel
 * @param startDate
 * @param endDate
 * @param dataThumbnailUrl
 * @param downloadCount
 * @param rowCount
 * @param columnCount
 * @param createdAt
 * @param countConnectedProjects
 */
public record ConnectedDataResponse(
        Long id,
        String title,
        String topicLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String dataThumbnailUrl,
        Integer downloadCount,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
